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

// [START drive_move_file_to_folder]


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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate use case for moving file to folder.*/
public class MoveFileToFolder {


    /**
     * @param realFileId Id of file to be moved.
     * @param realFolderId Id of folder where the fill will be moved.
     * @return list of parent ids for the file.
     * */
    private static List<String> moveFileToFolder(String realFileId, String realFolderId)
            throws IOException {
        /* Load pre-authorized user credentials from the environment.
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

        // Retrieve the existing parents to remove
        File file = service.files().get(realFileId)
                .setFields("parents")
                .execute();
        StringBuilder previousParents = new StringBuilder();
        for (ParentReference parent : file.getParents()) {
            previousParents.append(parent.getId());
            previousParents.append(',');
        }
        try {
            // Move the file to the new folder
            file = service.files().update(realFileId, null)
                    .setAddParents(realFolderId)
                    .setRemoveParents(previousParents.toString())
                    .setFields("id, parents")
                    .execute();

            List<String> parents = new ArrayList<String>();
            for (ParentReference parent : file.getParents()) {
                parents.add(parent.getId());
            }
            return parents;
        }catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to move file: " + e.getDetails());
            throw e;
        }
    }
}
// [END drive_move_file_to_folder]
