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
// [START drive_recover_team_drives]

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.TeamDrive;
import com.google.api.services.drive.model.TeamDriveList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* class to demonstrate use-case of Drive's recover all team drives without an organizer. */
public class RecoverTeamDrive {

    /**
     * Finds all Team Drives without an organizer and add one.
     * @param realUser User ID for the new organizer.
     * @return  All team drives without an organizer.
     * @throws IOException if service account credentials file not found.
     */
    public static List<TeamDrive> recoverTeamDrives(String realUser) throws IOException {
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
        List<TeamDrive> teamDrives = new ArrayList<TeamDrive>();

        // Find all Team Drives without an organizer and add one.
        // Note: This example does not capture all cases. Team Drives
        // that have an empty group as the sole organizer, or an
        // organizer outside the organization are not captured. A
        // more exhaustive approach would evaluate each Team Drive
        // and the associated permissions and groups to ensure an active
        // organizer is assigned.
        String pageToken = null;
        Permission newOrganizerPermission = new Permission()
                .setType("user")
                .setRole("organizer")
                .setValue("user@example.com");

        newOrganizerPermission.setValue(realUser);
        try {
            do {
                TeamDriveList result = service.teamdrives().list()
                        .setQ("organizerCount = 0")
                        .setUseDomainAdminAccess(true)
                        .setFields("nextPageToken, items(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                for (TeamDrive teamDrive : result.getItems()) {
                    System.out.printf("Found Team Drive without organizer: %s (%s)\n",
                            teamDrive.getName(), teamDrive.getId());
                    // Note: For improved efficiency, consider batching
                    // permission insert requests
                    Permission permissionResult = service.permissions()
                            .insert(teamDrive.getId(), newOrganizerPermission)
                            .setUseDomainAdminAccess(true)
                            .setSupportsTeamDrives(true)
                            .setFields("id")
                            .execute();
                    System.out.printf("Added organizer permission: %s\n",
                            permissionResult.getId());
                }

                teamDrives.addAll(result.getItems());

                pageToken = result.getNextPageToken();
            } while (pageToken != null);

            return teamDrives;
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to recover team drive: " + e.getDetails());
            throw e;
        }
    }
}
// [END drive_recover_team_drives]

