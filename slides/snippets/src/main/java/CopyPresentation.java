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

// [START slides_copy_presentation]

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.slides.v1.SlidesScopes;

import java.util.Arrays;
import java.util.List;

public class CopyPresentation {

    // Create a new presentation
    public static void copyPresentation(String sourcePresentationId) {
        try {
            // String sourcePresentationId = "55BYRgExa5NEv9vMegpPBaWESy7oKTK21l4P2DcX5l4";

            // Load user credentials from environment.
            // See <some URL> for additional ways to acquire credentials for
            // your application type and use case (CLI, web application, etc.)
            List<String> scopes = Arrays.asList(SlidesScopes.DRIVE);
            GoogleCredential credential = GoogleCredential.getApplicationDefault();
            credential = credential.createScoped(scopes);

            // Build the API client
            Drive drive = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Slides API Samples")
                    .build();

            String titleOfCopy = "Copy of Quarterly Report";
            File targetMetadata = new File().setName(titleOfCopy);
            targetMetadata = drive.files().copy(sourcePresentationId, targetMetadata).execute();

            System.out.println("Created presentation with ID: " + targetMetadata.getId());
        } catch (Exception e) {
            System.out.println("Error during copyPresentation: \n" + e.toString());
        }
    }
}
// [END slides_copy_presentation]
