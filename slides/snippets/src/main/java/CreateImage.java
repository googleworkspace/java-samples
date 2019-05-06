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

// [START slides_create_image]
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateImage {

    // Create a new presentation
    public static void createImage(String presentationId, String slideId) {
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


            String imageUrl = "https://picsum.photos/id/152/200/300";
            String imageId = "MyImageId_01";

            // Create a new image, using a supplied object ID, with content downloaded from imageUrl.
            Dimension emu4M = new Dimension().setMagnitude(4000000.0).setUnit("EMU");
            Size imageSize = new Size()
                    .setHeight(emu4M)
                    .setWidth(emu4M);
            AffineTransform transform = new AffineTransform()
                    .setScaleX(1.0)
                    .setScaleY(1.0)
                    .setTranslateX(100000.0)
                    .setTranslateY(100000.0)
                    .setUnit("EMU");
            PageElementProperties elementProps = new PageElementProperties()
                    .setPageObjectId(slideId)
                    .setSize(imageSize)
                    .setTransform(transform);
            CreateImageRequest createImage = new CreateImageRequest()
                    .setObjectId(imageId)
                    .setUrl(imageUrl)
                    .setElementProperties(elementProps);

            List<Request> requests = Collections.singletonList(new Request().setCreateImage(createImage));

            // Execute the request.
            BatchUpdatePresentationRequest body =
                    new BatchUpdatePresentationRequest().setRequests(requests);
            BatchUpdatePresentationResponse response =
                    slides.presentations().batchUpdate(presentationId, body).execute();
            CreateImageResponse createImageResponse = response.getReplies().get(0).getCreateImage();
            System.out.println("Created image with ID: " + createImageResponse.getObjectId());
        } catch (Exception e) {
            System.out.println("Error during createImage: \n" + e.toString());
        }
    }
}
// [END slides_create_image]
