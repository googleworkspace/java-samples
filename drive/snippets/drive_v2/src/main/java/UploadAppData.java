// Copyright 2022 Google LLC
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
// [START drive_upload_appdata]

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Class to demonstrate use-case of create file in the application data folder.
 */
public class UploadAppData {

  /**
   * Creates a file in the application data folder.
   *
   * @return Created file's Id.
   */
  public static String uploadAppData() throws IOException {
        /*Load pre-authorized user credentials from the environment.
        TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application.*/
    GoogleCredentials credentials = null;
    try {
      credentials = GoogleCredentials.getApplicationDefault()
          .createScoped(Arrays.asList(DriveScopes.DRIVE_APPDATA));
    } catch (IOException e) {
      e.printStackTrace();
    }
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Build a new authorized API client service.
    Drive service = new Drive.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Drive samples")
        .build();
    try {
      // File's metadata.
      File fileMetadata = new File();
      fileMetadata.setTitle("config.json");
      fileMetadata.setParents(Collections.singletonList(
          new ParentReference().setId("appDataFolder")));
      java.io.File filePath = new java.io.File("files/config.json");
      // Specify media type and file-path for file.
      FileContent mediaContent = new FileContent("application/json", filePath);
      File file = service.files().insert(fileMetadata, mediaContent)
          .setFields("id")
          .execute();
      System.out.println("File ID: " + file.getId());

      return file.getId();
    } catch (GoogleJsonResponseException e) {
      // TODO(developer) - handle error appropriately
      System.err.println("Unable to create file: " + e.getDetails());
      throw e;
    }
  }

}
// [END drive_upload_appdata]