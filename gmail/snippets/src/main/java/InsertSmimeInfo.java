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


// [START gmail_insert_smime_info]
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.SmimeInfo;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Gmail Insert Smime Certificate API*/
public class InsertSmimeInfo {
    /**
     * Upload an S/MIME certificate for the user.
     *
     * @param filename Name of the file containing the S/MIME certificate.
     * @param password Password for the certificate file, or null if the file
     *                is not password-protected.
     * @param userId User's email address.
     * @param sendAsEmail The "send as" email address, or null if it should be the same as userId.
     * @return An SmimeInfo object with details about the uploaded certificate.
     */
    public static SmimeInfo insertSmimeInfo(String filename,
                                            String password,
                                            String userId,
                                            String sendAsEmail)
            throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                        .createScoped(Collections.singletonList(GmailScopes.GMAIL_SETTINGS_SHARING));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Gmail samples")
                .build();

        if (sendAsEmail == null) {
            sendAsEmail = userId;
        }

        try {
            SmimeInfo smimeInfo = CreateSmimeInfo.createSmimeInfo(filename, password);
            SmimeInfo results = service.users().settings().sendAs().smimeInfo()
                    .insert(userId, sendAsEmail, smimeInfo)
                    .execute();
            System.out.printf("Inserted certificate, id: %s\n", results.getId());
            return results;
        } catch (IOException e) {
            System.err.printf("An error occured: %s", e);
        }

        return null;
    }
}
// [END gmail_insert_smime_info]