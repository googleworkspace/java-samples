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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.StartPageToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate use of Drive's manage changes API */
public class Change {
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
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes. DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final Drive service;
    public Change(Drive service) {
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
        InputStream in = Change.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        Change change = new Change(service);
        String startPageToken = change.fetchStartPageToken();
        String savedToken = change.fetchChanges(startPageToken);
        System.out.println("Saved Token: " + savedToken);
    }
    /**
     * Retrieve the start page token for the first time.
     * @return Start page token as String.
     * @throws IOException if file is not found
     */
    public String fetchStartPageToken() throws IOException {
        //Drive driveService = this.service;
        // [START drive_fetch_start_page_token]
        StartPageToken response = this.service.changes()
                .getStartPageToken().execute();
        System.out.println("Start token: " + response.getStartPageToken());
        // [END drive_fetch_start_page_token]
        return response.getStartPageToken();
    }
    /**
     * Retrieve the list of changes for the currently authenticated user.
     * @param savedStartPageToken Last saved start token for this user.
     * @return Saved token after last page.
     * @throws IOException if file is not found
     */
    public String fetchChanges(String savedStartPageToken) throws IOException {
        //Drive driveService = this.service;
        // [START drive_fetch_changes]
        // Begin with our last saved start token for this user or the
        // current token from getStartPageToken()
        String pageToken = savedStartPageToken;
        while (pageToken != null) {
            ChangeList changes = this.service.changes().list(pageToken)
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
        // [END drive_fetch_changes]
        return savedStartPageToken;
    }
}
