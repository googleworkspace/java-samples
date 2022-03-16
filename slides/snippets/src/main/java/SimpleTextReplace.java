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


// [START slides_simple_text_replace]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.DeleteTextRequest;
import com.google.api.services.slides.v1.model.InsertTextRequest;
import com.google.api.services.slides.v1.model.Range;
import com.google.api.services.slides.v1.model.Request;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Slides Replace Text API */
public class SimpleTextReplace {
    /**
     * Remove existing text in the shape, then insert new text.
     *
     * @param presentationId - id of the presentation.
     * @param shapeId - id of the shape.
     * @param replacementText - New replacement text.
     * @return response
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdatePresentationResponse simpleTextReplace(
            String presentationId, String shapeId, String replacementText) throws IOException {
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

        // Remove existing text in the shape, then insert the new text.
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setDeleteText(new DeleteTextRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("ALL"))));
        requests.add(new Request()
                .setInsertText(new InsertTextRequest()
                        .setObjectId(shapeId)
                        .setInsertionIndex(0)
                        .setText(replacementText)));

        BatchUpdatePresentationResponse response = null;
        try {
            // Execute the requests.
            BatchUpdatePresentationRequest body =
                    new BatchUpdatePresentationRequest().setRequests(requests);
            response = service.presentations().batchUpdate(presentationId, body).execute();
            System.out.println("Replaced text in shape with ID: " + shapeId);
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 400) {
                System.out.printf("Shape not found with id '%s'.\n", shapeId);
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
// [END slides_simple_text_replace]