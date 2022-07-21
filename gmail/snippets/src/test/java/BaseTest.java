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

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.Before;
import java.io.IOException;

public class BaseTest {

    public static final String TEST_USER = "ci-test01@workspacesamples.dev";
    public static final String RECIPIENT = "gduser1@workspacesamples.dev";
    public static final String FORWARDING_ADDRESS = "gduser1@workspacesamples.dev";

    protected Gmail service;

    /**
     * Create a default authorization Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException - if credentials file not found.
     */
    public Gmail buildService() throws IOException {
        /* Load pre-authorized user credentials from the environment.
         TODO(developer) - See https://developers.google.com/identity for
          guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(GmailScopes.GMAIL_SETTINGS_BASIC,
                GmailScopes.GMAIL_COMPOSE,
                GmailScopes.GMAIL_SETTINGS_SHARING,
                GmailScopes.GMAIL_LABELS);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Create the Gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName("Gmail API Snippets")
                    .build();
        return service;
    }

    @Before
    public void setupService() throws IOException {
        this.service = buildService();
    }
}
