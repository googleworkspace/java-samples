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


// [START slides_create_textbox_with_text]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.AffineTransform;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.CreateShapeRequest;
import com.google.api.services.slides.v1.model.CreateShapeResponse;
import com.google.api.services.slides.v1.model.Dimension;
import com.google.api.services.slides.v1.model.InsertTextRequest;
import com.google.api.services.slides.v1.model.PageElementProperties;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.Size;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Slides Create Textbox API */
public class CreateTextboxWithText {
    /**
     * Create a new square textbox, using the specified id.
     *
     * @param presentationId - id of the presentation.
     * @param slideId - id of the slide.
     * @param textBoxId - id for the textbox.
     * @return textbox id
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdatePresentationResponse createTextBoxWithText(
            String presentationId, String slideId, String textBoxId) throws IOException {
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

        // Create a new square text box, using a supplied object ID.
        List<Request> requests = new ArrayList<>();
        Dimension pt350 = new Dimension().setMagnitude(350.0).setUnit("PT");
        requests.add(new Request()
                .setCreateShape(new CreateShapeRequest()
                        .setObjectId(textBoxId)
                        .setShapeType("TEXT_BOX")
                        .setElementProperties(new PageElementProperties()
                                .setPageObjectId(slideId)
                                .setSize(new Size()
                                        .setHeight(pt350)
                                        .setWidth(pt350))
                                .setTransform(new AffineTransform()
                                        .setScaleX(1.0)
                                        .setScaleY(1.0)
                                        .setTranslateX(350.0)
                                        .setTranslateY(100.0)
                                        .setUnit("PT")))));

        // Insert text into the box, using the object ID given to it.
        requests.add(new Request()
                .setInsertText(new InsertTextRequest()
                        .setObjectId(textBoxId)
                        .setInsertionIndex(0)
                        .setText("New Box Text Inserted")));
        BatchUpdatePresentationResponse response = null;

        try {
            // Execute the requests.
            BatchUpdatePresentationRequest body =
                    new BatchUpdatePresentationRequest().setRequests(requests);
            response = service.presentations().batchUpdate(presentationId, body).execute();
            CreateShapeResponse createShapeResponse = response.getReplies().get(0).getCreateShape();
            System.out.println("Created textbox with ID: " + createShapeResponse.getObjectId());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Presentation not found with id '%s'.\n", presentationId);
            } else {
                throw e;
            }
        }
        return response;
    }
}
// [END slides_create_textbox_with_text]