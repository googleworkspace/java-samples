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

// [START slides_create_presentation]
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.Presentation;

import java.util.Arrays;
import java.util.List;

public class CreatePresentation {

    // Create a new presentation
    public static void createPresentation() {
        try {

            // Load user credentials from environment.
            // See <some URL> for additional ways to acquire credentials for
            // your application type and use case (CLI, web application, etc.)
            List<String> scopes = Arrays.asList(SlidesScopes.PRESENTATIONS);
            GoogleCredential credential = GoogleCredential.getApplicationDefault();
            credential = credential.createScoped(scopes);

            // Build the API client
            Slides slides = new Slides.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Slides API Samples")
                    .build();

            // Create the presentation
            String title = "Quarterly Report";
            Presentation presentation = new Presentation()
                    .setTitle(title);
            presentation = slides.presentations().create(presentation)
                    .setFields("presentationId")
                    .execute();

            System.out.println("Created presentation with ID: " + presentation.getPresentationId());
        } catch (Exception e) {
            System.out.println("Error during createPresentation: \n" + e.toString());
        }
    }
}
// [END slides_create_presentation]
