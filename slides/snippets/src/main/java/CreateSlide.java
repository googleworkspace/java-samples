// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


// [START slides_create_slide]
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.CreateSlideRequest;
import com.google.api.services.slides.v1.model.CreateSlideResponse;
import com.google.api.services.slides.v1.model.LayoutReference;
import com.google.api.services.slides.v1.model.Request;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Create Slides API */
public class CreateSlide {
    /**
     * Creates a new slide.
     *
     * @param presentationId - id of the presentation.
     * @param slideId - id for the new slide.
     * @return slide id
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdatePresentationResponse createSlide(String presentationId,
                                                              String slideId) throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(SlidesScopes.PRESENTATIONS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the slides API client
        Slides service = new Slides.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        // Add a slide at index 1 using the predefined "TITLE_AND_TWO_COLUMNS" layout
        List<Request> requests = new ArrayList<>();
        BatchUpdatePresentationResponse response = null;
        try {
            requests.add(new Request()
                    .setCreateSlide(new CreateSlideRequest()
                            .setObjectId(slideId)
                            .setInsertionIndex(1)
                            .setSlideLayoutReference(new LayoutReference()
                                    .setPredefinedLayout("TITLE_AND_TWO_COLUMNS"))));

            // If you wish to populate the slide with elements, add create requests here,
            // using the slide ID specified above.

            // Execute the request.
            BatchUpdatePresentationRequest body =
                    new BatchUpdatePresentationRequest().setRequests(requests);
            response = service.presentations().batchUpdate(presentationId, body).execute();
            CreateSlideResponse createSlideResponse = response.getReplies().get(0).getCreateSlide();
            // Prints the slide id.
            System.out.println("Created slide with ID: " + createSlideResponse.getObjectId());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 400) {
                System.out.printf(" Id '%s' should be unique among all pages and page elements.\n", presentationId);
            } else if (error.getCode() == 404) {
                System.out.printf("Presentation not found with id '%s'.\n", presentationId);
            }
            else {
                throw e;
            }
        }
        return response;
    }
}
// [END slides_create_slide]