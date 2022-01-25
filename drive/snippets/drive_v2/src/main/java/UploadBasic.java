// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START drive_upload_basic]

import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Arrays;

/* Class to demonstrate use of Drive insert file API */
public class UploadBasic {

    /**
     * Upload new file.
     * @return Inserted file metadata if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    private static File uploadBasic() throws IOException{
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();
        // Upload file photo.jpg on drive.
        File fileMetadata = new File();
        fileMetadata.setTitle("photo.jpg");
        // File's content.
        java.io.File filePath = new java.io.File("files/photo.jpg");
        // Specify media type and file-path for file.
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        try {
            File file = service.files().insert(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file;
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
            return null;
        }
    }
}
// [END drive_upload_basic]
