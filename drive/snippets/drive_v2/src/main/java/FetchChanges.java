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
// [START drive_fetch_changes]

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.StartPageToken;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Arrays;

/* Class to demonstrate use-case of Drive's fetch changes in file. */
public class FetchChanges {
    /**
     * Retrieve the list of changes for the currently authenticated user.
     * @param savedStartPageToken Last saved start token for this user.
     * @return Saved token after last page.
     * @throws IOException if file is not found
     */
    private static String fetchChanges(String savedStartPageToken) throws IOException {

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
        try {
            // Begin with our last saved start token for this user or the
            // current token from getStartPageToken()
            String pageToken = savedStartPageToken;
            while (pageToken != null) {
                ChangeList changes = service.changes().list(pageToken)
                        .execute();
                for (com.google.api.services.drive.model.Change change : changes.getChanges()) {
                    // Process change
                    System.out.println("Change found for file: " + change.getFileId());
                }
                if (changes.getNewStartPageToken() != null) {
                    // Last page, save this token for the next polling interval
                    savedStartPageToken = changes.getNewStartPageToken();
                }
                pageToken = changes.getNextPageToken();
            }

            return savedStartPageToken;
        }catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to fetch changes: " + e.getDetails());
            throw e;
        }
    }
}
// [END drive_fetch_changes]
