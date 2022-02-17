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

// [START drive_share_file]

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate use-case of modify permissions. */
public class ShareFile {

    /**
     * Batch permission modification.
     * realFileId file Id.
     * realUser User Id.
     * realDomain Domain of the user ID.
     * @return list of modified permissions if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    private static List<String> shareFile(String realFileId, String realUser, String realDomain) throws IOException{
        /* Load pre-authorized user credentials from the environment.
         TODO(developer) - See https://developers.google.com/identity for
         guides on implementing OAuth2 for your application.application*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();

        final List<String> ids = new ArrayList<String>();


        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                // Handle error
                System.err.println(e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                System.out.println("Permission ID: " + permission.getId());

                ids.add(permission.getId());

            }
        };
        BatchRequest batch = service.batch();
        Permission userPermission = new Permission()
                .setType("user")
                .setRole("writer");

        userPermission.setValue(realUser);
        try {
            service.permissions().insert(realFileId, userPermission)
                    .setFields("id")
                    .queue(batch, callback);

            Permission domainPermission = new Permission()
                    .setType("domain")
                    .setRole("reader");

            domainPermission.setValue(realDomain);

            service.permissions().insert(realFileId, domainPermission)
                    .setFields("id")
                    .queue(batch, callback);

            batch.execute();

            return ids;
        }catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to modify permission: " + e.getDetails());
            throw e;
        }
    }
}
// [END drive_share_file]
