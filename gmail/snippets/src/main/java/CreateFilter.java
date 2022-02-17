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


// [START gmail_create_filter]
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Filter;
import com.google.api.services.gmail.model.FilterAction;
import com.google.api.services.gmail.model.FilterCriteria;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Arrays;

/* Class to demonstrate the use of Gmail Create Filter API */
public class CreateFilter {
    /**
     * Create a new filter.
     *
     * @param realLabelId - ID of the user label to add
     * @return the created filter id
     * @throws IOException - if service account credentials file not found.
     */
    public static String createNewFilter(String realLabelId) throws IOException {
        // TODO(developer) - Replace with your email address.
        String USER_EMAIL_ADDRESS = "gduser1@workspacesamples.dev";

        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Arrays.asList(GmailScopes.GMAIL_SETTINGS_BASIC, GmailScopes.GMAIL_LABELS))
                .createDelegated(USER_EMAIL_ADDRESS);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

         // Create the gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Gmail samples")
                .build();

        // [START createFilter]
        String labelId = "Label_14"; // ID of the user label to add
        // [START_EXCLUDE silent]
        labelId = realLabelId;
        // [END_EXCLUDE]

        try {
            // Filter the mail from sender and archive them(skip the inbox)
            Filter filter = new Filter()
                    .setCriteria(new FilterCriteria()
                            .setFrom("gduser2@workspacesamples.dev"))
                    .setAction(new FilterAction()
                            .setAddLabelIds(Arrays.asList(labelId))
                            .setRemoveLabelIds(Arrays.asList("INBOX")));

            Filter result = service.users().settings().filters().create("me", filter).execute();
            // Prints the new created filter ID
            System.out.println("Created filter " + result.getId());
            return result.getId();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create filter: " + e.getDetails());
            throw e;
        }
    }
}
// [END gmail_create_filter]
