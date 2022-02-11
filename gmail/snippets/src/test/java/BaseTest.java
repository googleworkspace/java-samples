// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.junit.Before;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/* Class to initialize the services for Gmail API*/
public class BaseTest {

    public static final String TEST_USER = "gduser1@workspacesamples.dev";
    public static final String RECIPIENT = "gduser2@workspacesamples.dev";
    public static final String FORWARDING_ADDRESS = "gduser2@workspacesamples.dev";

    static {
        enableLogging();
    }

    protected Gmail service;

    public static void enableLogging() {
        Logger logger = Logger.getLogger(HttpTransport.class.getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(new Handler() {

            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(LogRecord record) {
                // default ConsoleHandler will print >= INFO to System.err
                if (record.getLevel().intValue() < Level.INFO.intValue()) {
                    System.out.println(record.getMessage());
                }
            }
        });
    }

    // Load pre-authorized user credentials from the environment.
    // TODO(developer) - See https://developers.google.com/identity for
    // guides on implementing OAuth2 for your application.
    public GoogleCredentials getCredential() throws IOException {
        GoogleCredentials defaultCredentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Arrays.asList(GmailScopes.GMAIL_COMPOSE,
                        GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS,
                        GmailScopes.GMAIL_SETTINGS_BASIC,
                        GmailScopes.GMAIL_SETTINGS_SHARING))
                .createDelegated(TEST_USER);
        return defaultCredentials;
    }

    // Create the gmail API client
    public Gmail buildService() throws IOException {
        GoogleCredentials credential = getCredential();
        return new Gmail.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credential))
                .setApplicationName("Gmail API Snippets")
                .build();
    }

    // Call the gmail services before test method
    @Before
    public void setup() throws IOException {
        this.service = buildService();
    }

}