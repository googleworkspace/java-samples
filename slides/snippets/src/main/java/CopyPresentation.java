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


// [START slides_copy_presentation]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Slides Copy Presentation API */
public class CopyPresentation {
    /**
     * Copy an existing presentation.
     *
     * @param presentationId - id of the presentation.
     * @param copyTitle - New title of the presentation.
     * @return presentation id
     * @throws IOException - if credentials file not found.
     */
    public static String copyPresentation(String presentationId, String copyTitle) throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(SlidesScopes.DRIVE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the drive API client
        Drive driveService = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        String presentationCopyId = null;
        try {
            // Copies an existing presentation using specified presentation title.
            File copyMetadata = new File().setName(copyTitle);
            File presentationCopyFile =
                    driveService.files().copy(presentationId, copyMetadata).execute();
            presentationCopyId = presentationCopyFile.getId();
            // Prints the new copied presentation id.
            System.out.println("New copied presentation id "+presentationCopyId);
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Presentation not found with id '%s'.\n", presentationId);
            } else {
                throw e;
            }
        }
        return presentationCopyId;
    }
}
// [END slides_copy_presentation]