// Copyright 2024 Google LLC
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

// [START chat_quickstart]

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.ChatServiceSettings;
import com.google.chat.v1.ListSpacesRequest;
import com.google.chat.v1.Space;
import com.google.protobuf.util.JsonFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/* class to demonstrate use of Google Chat API spaces list API */
public class ChatQuickstart {
  /** Directory to store authorization tokens for this application. */
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
   * your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES =
      Collections.singletonList("https://www.googleapis.com/auth/chat.spaces.readonly");

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  /**
   * Run the OAuth2 flow for local/installed app.
   *
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credentials getCredentials() throws Exception {
    // Load client secrets.
    InputStream credentialsFileInputStream =
        ChatQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (credentialsFileInputStream == null) {
      throw new FileNotFoundException("Credentials file resource not found.");
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentialsFileInputStream));

    // Set up authorization code flow.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
            // Set these two options to generate refresh token alongside access token.
            .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();

    // Authorize.
    Credential credential =
        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

    // Build and return an authorized Credential object
    AccessToken accessToken =
        new AccessToken(
            credential.getAccessToken(),
            new Date(
                // put the actual expiry date of access token here
                System.currentTimeMillis()));
    return UserCredentials.newBuilder()
        .setAccessToken(accessToken)
        .setRefreshToken(credential.getRefreshToken())
        .setClientId(clientSecrets.getInstalled().getClientId())
        .setClientSecret(clientSecrets.getInstalled().getClientSecret())
        .build();
  }

  public static void main(String... args) throws Exception {
    // Override default service settings to supply user credentials.
    Credentials credentials = getCredentials();

    // Create the ChatServiceSettings with the credentials
    ChatServiceSettings chatServiceSettings =
        ChatServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();

    try (ChatServiceClient chatServiceClient = ChatServiceClient.create(chatServiceSettings)) {
      ListSpacesRequest request =
          ListSpacesRequest.newBuilder()
              // Filter spaces by space type (SPACE or GROUP_CHAT or
              // DIRECT_MESSAGE).
              .setFilter("spaceType = \"SPACE\"")
              .build();

      // Iterate over results and resolve additional pages automatically.
      for (Space response : chatServiceClient.listSpaces(request).iterateAll()) {
        System.out.println(JsonFormat.printer().print(response));
      }
    }
  }
}
// [END chat_quickstart]
