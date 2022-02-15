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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Drive;
import com.google.api.services.drive.model.DriveList;
import com.google.api.services.drive.model.Permission;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* class to demonstrate use-case of Drive's shared drive without an organizer. */
public class RecoverDrive {
    private com.google.api.services.drive.Drive service;
    /** Application name. */
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes. DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Constructor that initialize Drive API client service.
     */
    public RecoverDrive(com.google.api.services.drive.Drive service) {
        this.service = service;
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = RecoverDrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        RecoverDrive drive = new RecoverDrive(service);
        // replace below email id value with actual user email.
        drive.recoverDrives("test@gmail.com");
    }

    /**
     * @param  realUser User's email id.
     * @return All shared drives without an organizer.
     * @throws IOException if shared drive not found.
     */
    public List<Drive> recoverDrives(String realUser)
            throws IOException {
        com.google.api.services.drive.Drive driveService = this.service;
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

        return drives;
    }
}
// [END drive_recover_drives]

