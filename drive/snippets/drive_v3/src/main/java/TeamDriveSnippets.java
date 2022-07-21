/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.TeamDrive;
import com.google.api.services.drive.model.TeamDriveList;
import com.google.api.services.drive.model.Permission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamDriveSnippets {
  private Drive service;

  public TeamDriveSnippets(Drive service) {
    this.service = service;
  }

  public String createTeamDrive() throws IOException {
    Drive driveService = this.service;
    // [START createTeamDrive]
    TeamDrive teamDriveMetadata = new TeamDrive();
    teamDriveMetadata.setName("Project Resources");
    String requestId = UUID.randomUUID().toString();
    TeamDrive teamDrive = driveService.teamdrives().create(requestId,
        teamDriveMetadata)
        .execute();
    System.out.println("Team Drive ID: " + teamDrive.getId());
    // [END createTeamDrive]
    return teamDrive.getId();
  }

  public List<TeamDrive> recoverTeamDrives(String realUser)
      throws IOException {
    Drive driveService = this.service;
    List<TeamDrive> teamDrives = new ArrayList<TeamDrive>();
    // [START recoverTeamDrives]
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
        .setEmailAddress("user@example.com");
    // [START_EXCLUDE silent]
    newOrganizerPermission.setEmailAddress(realUser);
    // [END_EXCLUDE]

    do {
      TeamDriveList result = driveService.teamdrives().list()
          .setQ("organizerCount = 0")
          .setFields("nextPageToken, teamDrives(id, name)")
          .setUseDomainAdminAccess(true)
          .setPageToken(pageToken)
          .execute();
      for (TeamDrive teamDrive : result.getTeamDrives()) {
        System.out.printf("Found Team Drive without organizer: %s (%s)\n",
            teamDrive.getName(), teamDrive.getId());
        // Note: For improved efficiency, consider batching
        // permission insert requests
        Permission permissionResult = driveService.permissions()
            .create(teamDrive.getId(), newOrganizerPermission)
            .setUseDomainAdminAccess(true)
            .setSupportsTeamDrives(true)
            .setFields("id")
            .execute();
        System.out.printf("Added organizer permission: %s\n",
            permissionResult.getId());

      }
      // [START_EXCLUDE silent]
      teamDrives.addAll(result.getTeamDrives());
      // [END_EXCLUDE]
      pageToken = result.getNextPageToken();
    } while (pageToken != null);
    // [END recoverTeamDrives]
    return teamDrives;
  }
}
