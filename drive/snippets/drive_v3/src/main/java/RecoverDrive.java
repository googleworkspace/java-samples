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
// [START drive_recover_drives]


import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Drive;
import com.google.api.services.drive.model.DriveList;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* class to demonstrate use-case of Drive's shared drive without an organizer. */
public class RecoverDrive {

    /**
     * Find all shared drives without an organizer and add one.
     * @param  realUser User's email id.
     * @return All shared drives without an organizer.
     * @throws IOException if shared drive not found.
     */
    public static List<Drive> recoverDrives(String realUser)
            throws IOException {
        /*Load pre-authorized user credentials from the environment.
        TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();
        List<Drive> drives = new ArrayList<Drive>();

        // Find all shared drives without an organizer and add one.
        // Note: This example does not capture all cases. Shared drives
        // that have an empty group as the sole organizer, or an
        // organizer outside the organization are not captured. A
        // more exhaustive approach would evaluate each shared drive
        // and the associated permissions and groups to ensure an active
        // organizer is assigned.
        String pageToken = null;
        Permission newOrganizerPermission = new Permission()
                .setType("user")
                .setRole("organizer");

        newOrganizerPermission.setEmailAddress(realUser);


        do {
            DriveList result = service.drives().list()
                    .setQ("organizerCount = 0")
                    .setFields("nextPageToken, drives(id, name)")
                    .setUseDomainAdminAccess(true)
                    .setPageToken(pageToken)
                    .execute();
            for (Drive drive : result.getDrives()) {
                System.out.printf("Found drive without organizer: %s (%s)\n",
                        drive.getName(), drive.getId());
                // Note: For improved efficiency, consider batching
                // permission insert requests
                Permission permissionResult = service.permissions()
                        .create(drive.getId(), newOrganizerPermission)
                        .setUseDomainAdminAccess(true)
                        .setSupportsAllDrives(true)
                        .setFields("id")
                        .execute();
                System.out.printf("Added organizer permission: %s\n",
                        permissionResult.getId());

            }

            drives.addAll(result.getDrives());

            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return drives;
    }
}
// [END drive_recover_drives]

