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

import com.google.api.services.drive.model.Drive;
import com.google.api.services.drive.model.DriveList;
import com.google.api.services.drive.model.Permission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DriveSnippets {
  private com.google.api.services.drive.Drive service;

  public DriveSnippets(com.google.api.services.drive.Drive service) {
    this.service = service;
  }

  public String createDrive() throws IOException {
    com.google.api.services.drive.Drive driveService = this.service;
    // [START createDrive]
    Drive driveMetadata = new Drive();
    driveMetadata.setName("Project Resources");
    String requestId = UUID.randomUUID().toString();
    Drive drive = driveService.drives().insert(requestId, driveMetadata)
            .execute();
    System.out.println("Drive ID: " + drive.getId());
    // [END createDrive]
    return drive.getId();
  }

  public List<Drive> recoverDrives(String realUser)
          throws IOException {
    com.google.api.services.drive.Drive driveService = this.service;
    List<Drive> drives = new ArrayList<Drive>();
    // [START recoverDrives]
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
            .setRole("organizer")
            .setValue("user@example.com");
    // [START_EXCLUDE silent]
    newOrganizerPermission.setValue(realUser);
    // [END_EXCLUDE]

    do {
      DriveList result = driveService.drives().list()
              .setQ("organizerCount = 0")
              .setUseDomainAdminAccess(true)
              .setFields("nextPageToken, items(id, name)")
              .setPageToken(pageToken)
              .execute();
      for (Drive drive : result.getItems()) {
        System.out.printf("Found drive without organizer: %s (%s)\n",
                drive.getName(), drive.getId());
        // Note: For improved efficiency, consider batching
        // permission insert requests
        Permission permissionResult = driveService.permissions()
                .insert(drive.getId(), newOrganizerPermission)
                .setUseDomainAdminAccess(true)
                .setSupportsAllDrives(true)
                .setFields("id")
                .execute();
        System.out.printf("Added organizer permission: %s\n",
                permissionResult.getId());

      }
      // [START_EXCLUDE silent]
      drives.addAll(result.getItems());
      // [END_EXCLUDE]
      pageToken = result.getNextPageToken();
    } while (pageToken != null);
    // [END recoverDrives]
    return drives;
  }
}
