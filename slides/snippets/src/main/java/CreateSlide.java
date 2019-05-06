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

// [START slides_create_slide]
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.CreateSlideRequest;
import com.google.api.services.slides.v1.model.CreateSlideResponse;
import com.google.api.services.slides.v1.model.LayoutReference;
import com.google.api.services.slides.v1.model.Request;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateSlide {

    // Create a new presentation
    public static void createSlide(String presentationId) {
        try {
            // String presentationId = "55BYRgExa5NEv9vMegpPBaWESy7oKTK21l4P2DcX5l4";

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

            // Add a new slide to the presentation
            String slideId = "MyNewSlide_001";
            int insertionIndex = 1;
            LayoutReference layout = new LayoutReference().setPredefinedLayout("TITLE_AND_TWO_COLUMNS");
            CreateSlideRequest createSlide = new CreateSlideRequest()
                    .setObjectId(slideId)
                    .setInsertionIndex(insertionIndex)
                    .setSlideLayoutReference(layout);
            Request request = new Request().setCreateSlide(createSlide);

            // Add request(s) to batch
            BatchUpdatePresentationRequest batch = new BatchUpdatePresentationRequest()
                    .setRequests(Collections.singletonList(request));

            // Execute the request.
            BatchUpdatePresentationResponse response = slides.presentations().batchUpdate(presentationId, batch)
                    .execute();
            CreateSlideResponse createSlideResponse = response.getReplies().get(0).getCreateSlide();
            System.out.println("Created slide with ID: " + createSlideResponse.getObjectId());
        } catch (Exception e) {
            System.out.println("Error during createSlide: \n" + e.toString());
        }
    }
}
// [END slides_create_slide]
