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


// [START gmail_enable_forwarding]
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.AutoForwarding;
import com.google.api.services.gmail.model.ForwardingAddress;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Gmail Enable Forwarding API */
public class EnableForwarding {
    /**
     * Enable the auto-forwarding for an account.
     *
     * @param forwardingEmail - Email address of the recipient whose email will be forwarded.
     * @return forwarding id and metadata, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public static AutoForwarding enableAutoForwarding(String forwardingEmail) throws IOException{
        // TODO(developer) - Replace with your email address.
        String USER_EMAIL_ADDRESS = "gduser1@workspacesamples.dev";

        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singletonList(GmailScopes.GMAIL_SETTINGS_SHARING))
                .createDelegated(USER_EMAIL_ADDRESS);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Gmail samples")
                .build();

        try{
            // Enable auto-forwarding and move forwarded messages to the trash
            ForwardingAddress address = new ForwardingAddress()
                    .setForwardingEmail(forwardingEmail);
            ForwardingAddress createAddressResult = service.users().settings().forwardingAddresses()
                    .create("me", address).execute();
            if (createAddressResult.getVerificationStatus().equals("accepted")) {
                AutoForwarding autoForwarding = new AutoForwarding()
                        .setEnabled(true)
                        .setEmailAddress(address.getForwardingEmail())
                        .setDisposition("trash");
                autoForwarding = service.users().settings().updateAutoForwarding("me", autoForwarding).execute();
                System.out.println(autoForwarding.toPrettyString());
                return autoForwarding;
            }
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to enable forwarding : " + e.getDetails());
            throw e;
        }
        return null;
    }
}
// [END gmail_enable_forwarding]
