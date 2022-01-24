// Copyright 2021 Google LLC
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


// [START gmail_update_signature]
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.SendAs;
import com.google.api.services.gmail.model.ListSendAsResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Gmail Update Signature API */
public class UpdateSignature {
    /**
     * Update the gmail signature.
     *
     * @throws IOException
     */
    public static void updateGmailSignature() throws IOException {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Collections.singleton(GmailScopes.GMAIL_SETTINGS_BASIC));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Gmail samples")
                .build();

        try {
            SendAs primaryAlias = null;
            ListSendAsResponse aliases = service.users().settings().sendAs().list("me").execute();
            for (SendAs alias : aliases.getSendAs()) {
                if (alias.getIsPrimary()) {
                    primaryAlias = alias;
                    break;
                }
            }
            // Updating a new signature
            SendAs aliasSettings = new SendAs().setSignature("I heart cats.");
            SendAs result = service.users().settings().sendAs().patch(
                            "me",
                            primaryAlias.getSendAsEmail(),
                            aliasSettings)
                    .execute();
            //Prints the updated signature
            System.out.println("Updated signature - " + result.getSignature());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to update signature: " + e.getDetails());
            throw e;
        }
    }
}
// [END gmail_update_signature]