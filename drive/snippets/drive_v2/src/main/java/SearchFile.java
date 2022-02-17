/* Copyright 2022 Google LLC

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.*/

// [START drive_search_files]

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate use-case of search files. */
public class SearchFile {

    /**
     * Search for specific set of files.
     * @return search result list.
     * @throws IOException if service account credentials file not found.
     */
    private static List<File> searchFile() throws IOException{
           /*Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
           guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();

        List<File> files = new ArrayList<File>();

        String pageToken = null;
        do {
            FileList result = service.files().list()
                    .setQ("mimeType='image/jpeg'")
                    .setSpaces("drive")
                    .setFields("nextPageToken, items(id, title)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getItems()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getTitle(), file.getId());
            }

            files.addAll(result.getItems());

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return files;
    }
}
// [END drive_search_files]
