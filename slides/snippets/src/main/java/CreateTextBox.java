/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// [START slides_create_textbox_with_text]
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateTextBox {

    // Create a new presentation
    public static void createTextBox(String presentationId, String slideId) {
        try {
            // String presentationId = "55BYRgExa5NEv9vMegpPBaWESy7oKTK21l4P2DcX5l4";
            // String slideId = "MySlide_01";

            // Load user credentials from environment.
            // See <some URL> for additional ways to acquire credentials for
            // your application type and use case (CLI, web application, etc.)
            List<String> scopes = Arrays.asList(SlidesScopes.PRESENTATIONS);
            GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(scopes);

            // Build the API client
            Slides slides = new Slides.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Slides API Samples")
                    .build();

            List<Request> requests = new ArrayList<>();
            String textBoxId = "MyTextBox_01";
            Dimension pt350 = new Dimension().setMagnitude(350.0).setUnit("PT");
            Size textBoxSize = new Size()
                    .setHeight(pt350)
                    .setWidth(pt350);
            AffineTransform transform = new AffineTransform()
                    .setScaleX(1.0)
                    .setScaleY(1.0)
                    .setTranslateX(350.0)
                    .setTranslateY(100.0)
                    .setUnit("PT");
            PageElementProperties elementProps = new PageElementProperties()
                    .setPageObjectId(slideId)
                    .setSize(textBoxSize)
                    .setTransform(transform);
            CreateShapeRequest createShape = new CreateShapeRequest()
                    .setObjectId(textBoxId)
                    .setShapeType("TEXT_BOX")
                    .setElementProperties(elementProps);
            requests.add(new Request().setCreateShape(createShape));

            // Insert text into the box, using the object ID given to it.
            InsertTextRequest insertText = new InsertTextRequest()
                    .setObjectId(textBoxId)
                    .setInsertionIndex(0)
                    .setText("New Box Text Inserted");
            requests.add(new Request().setInsertText(insertText));

            // Execute the requests.
            BatchUpdatePresentationRequest body =
                    new BatchUpdatePresentationRequest().setRequests(requests);
            BatchUpdatePresentationResponse response =
                    slides.presentations().batchUpdate(presentationId, body).execute();
            CreateShapeResponse createShapeResponse = response.getReplies().get(0).getCreateShape();
            System.out.println("Created textbox with ID: " + createShapeResponse.getObjectId());
        } catch (Exception e) {
            System.out.println("Error during createTextBox: \n" + e.toString());
        }
    }
}
// [END slides_create_textbox_with_text]
