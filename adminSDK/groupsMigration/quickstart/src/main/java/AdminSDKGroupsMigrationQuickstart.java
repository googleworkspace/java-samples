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

// [START admin_sdk_groups_migration_quickstart]
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.groupsmigration.GroupsMigration;
import com.google.api.services.groupsmigration.GroupsMigrationScopes;
import com.google.api.services.groupsmigration.model.Groups;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class AdminSDKGroupsMigrationQuickstart {
    private static final String APPLICATION_NAME = "Google Admin SDK Groups Migration API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FOLDER = "credentials"; // Directory to store user credentials.

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved credentials folder at /secret.
     */
    private static final List<String> SCOPES = Collections.singletonList(GroupsMigrationScopes.APPS_GROUPS_MIGRATION);
    private static final String CLIENT_SECRET_DIR = "client_secret.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If there is no client_secret.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = AdminSDKGroupsMigrationQuickstart.class.getResourceAsStream(CLIENT_SECRET_DIR);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException, MessagingException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GroupsMigration service = new GroupsMigration.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        System.out.println("Warning: A test email will be inserted " +
                "into the group entered below.");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print("Enter the email address of a Google Group in your domain: ");
        String groupId = scanner.nextLine().trim();

        // Insert a test email into the group.
        Session session = Session.getDefaultInstance(new java.util.Properties());
        MimeMessage message = new MimeMessage(session);
        message.setSubject("Group Migration API Test");
        message.setText("This is a test.\n");
        message.setSentDate(new java.util.Date());
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(groupId));
        message.setFrom(new InternetAddress("alice@example.com", "Alice Smith"));

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        message.writeTo(baos);
        ByteArrayContent content = new ByteArrayContent("message/rfc822", baos.toByteArray());
        Groups result = service.archive().insert(groupId, content).execute();
        System.out.printf("Result: %s\n", result.getResponseCode());
    }
}
// [END admin_sdk_groups_migration_quickstart]
