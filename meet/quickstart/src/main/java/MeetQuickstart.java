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

// [START meet_quickstart]

import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.CreateSpaceRequest;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ClientId;
import com.google.auth.oauth2.DefaultPKCEProvider;
import com.google.auth.oauth2.TokenStore;
import com.google.auth.oauth2.UserAuthorizer;

/* class to demonstrate use of Drive files list API */
public class MeetQuickstart {
  /**
   * Directory to store authorization tokens for this application.
   */
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  /**
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Collections
      .singletonList("https://www.googleapis.com/auth/meetings.space.created");

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  private static final String USER = "default";

  // Simple file-based token storage for storing oauth tokens
  private static final TokenStore TOKEN_STORE = new TokenStore() {
    private Path pathFor(String id) {
      return Paths.get(".", TOKENS_DIRECTORY_PATH, id + ".json");
    }

    @Override
    public String load(String id) throws IOException {
      if (!Files.exists(pathFor(id))) {
        return null;
      }
      return Files.readString(pathFor(id));
    }

    @Override
    public void store(String id, String token) throws IOException {
      Files.createDirectories(Paths.get(".", TOKENS_DIRECTORY_PATH));
      Files.writeString(pathFor(id), token);
    }

    @Override
    public void delete(String id) throws IOException {
      if (!Files.exists(pathFor(id))) {
        return;
      }
      Files.delete(pathFor(id));
    }
  };

  /**
   * Initialize a UserAuthorizer for local authorization.
   * 
   * @param callbackUri
   * @return
   */
  private static UserAuthorizer getAuthorizer(URI callbackUri) throws IOException {
    // Load client secrets.
    try (InputStream in = MeetQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
      if (in == null) {
        throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
      }

      ClientId clientId = ClientId.fromStream(in);

      UserAuthorizer authorizer = UserAuthorizer.newBuilder()
          .setClientId(clientId)
          .setCallbackUri(callbackUri)
          .setScopes(SCOPES)
          .setPKCEProvider(new DefaultPKCEProvider() {
            // Temporary fix for https://github.com/googleapis/google-auth-library-java/issues/1373
            @Override
            public String getCodeChallenge() {
              return super.getCodeChallenge().split("=")[0];
            }
          })
          .setTokenStore(TOKEN_STORE).build();
      return authorizer;
    }
  }

  /**
   * Run the OAuth2 flow for local/installed app.
   * 
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credentials getCredentials()
      throws Exception {

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().build();
    try {
      URI callbackUri = URI.create(receiver.getRedirectUri());
      UserAuthorizer authorizer = getAuthorizer(callbackUri);

      Credentials credentials = authorizer.getCredentials(USER);
      if (credentials != null) {
        return credentials;
      }

      URL authorizationUrl = authorizer.getAuthorizationUrl(USER, "", null);
      if (Desktop.isDesktopSupported() && 
          Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(authorizationUrl.toURI());
      } else {
        System.out.printf("Open the following URL to authorize access: %s\n",
            authorizationUrl.toExternalForm());
      }

      String code = receiver.waitForCode();
      credentials = authorizer.getAndStoreCredentialsFromCode(USER, code, callbackUri);
      return credentials;
    } finally {
      receiver.stop();
    }
  }

  public static void main(String... args) throws Exception {
    // Override default service settings to supply user credentials.
    Credentials credentials = getCredentials();
    SpacesServiceSettings settings = SpacesServiceSettings.newBuilder()
        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
        .build();

    try (SpacesServiceClient spacesServiceClient = SpacesServiceClient.create(settings)) {
      CreateSpaceRequest request = CreateSpaceRequest.newBuilder()
          .setSpace(Space.newBuilder().build())
          .build();
      Space response = spacesServiceClient.createSpace(request);
      System.out.printf("Space created: %s\n", response.getMeetingUri());
    } catch (IOException e) {
      // TODO(developer): Handle errors
      e.printStackTrace();
    }
  }
}
// [END meet_quickstart]
