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


// [START gmail_update_smime_certs]
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListSmimeInfoResponse;
import com.google.api.services.gmail.model.SmimeInfo;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

/* Class to demonstrate the use of Gmail Update Smime Certificate API*/
public class UpdateSmimeCerts {
    /**
     * Update S/MIME certificates for the user.
     *
     * <p>First performs a lookup of all certificates for a user. If there are no certificates, or
     * they all expire before the specified date/time, uploads the certificate in the specified file.
     * If the default certificate is expired or there was no default set, chooses the certificate with
     * the expiration furthest into the future and sets it as default.
     *
     * @param userId User's email address.
     * @param sendAsEmail The "send as" email address, or None if it should be the same as user_id.
     * @param certFilename Name of the file containing the S/MIME certificate.
     * @param certPassword Password for the certificate file, or None if the file is not
     *     password-protected.
     * @param expireTime DateTime object against which the certificate expiration is compared. If
     *     None, uses the current time. @ returns: The ID of the default certificate.
     * @return The ID of the default certifcate.
     */
    public static String updateSmimeCerts(String userId,
                                          String sendAsEmail,
                                          String certFilename,
                                          String certPassword,
                                          LocalDateTime expireTime)
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

        ListSmimeInfoResponse listResults;
        try {
            listResults = service.users().settings().sendAs().smimeInfo().list(userId, sendAsEmail).execute();
        } catch (IOException e) {
            System.err.printf("An error occurred during list: %s\n", e);
            return null;
        }

        String defaultCertId = null;
        String bestCertId = null;
        LocalDateTime bestCertExpire = LocalDateTime.MIN;

        if (expireTime == null) {
            expireTime = LocalDateTime.now();
        }
        if (listResults != null && listResults.getSmimeInfo() != null) {
            for (SmimeInfo smimeInfo : listResults.getSmimeInfo()) {
                String certId = smimeInfo.getId();
                boolean isDefaultCert = smimeInfo.getIsDefault();
                if (isDefaultCert) {
                    defaultCertId = certId;
                }
                LocalDateTime exp =
                        LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(smimeInfo.getExpiration()), ZoneId.systemDefault());
                if (exp.isAfter(expireTime)) {
                    if (exp.isAfter(bestCertExpire)) {
                        bestCertId = certId;
                        bestCertExpire = exp;
                    }
                } else {
                    if (isDefaultCert) {
                        defaultCertId = null;
                    }
                }
            }
        }
        if (defaultCertId == null) {
            String defaultId = bestCertId;
            if (defaultId == null && certFilename != null) {
                SmimeInfo insertResults = InsertSmimeInfo.insertSmimeInfo(certFilename,
                        certPassword,
                        userId,
                        sendAsEmail);
                if (insertResults != null) {
                    defaultId = insertResults.getId();
                }
            }

            if (defaultId != null) {
                try {
                    service.users().settings().sendAs().smimeInfo().setDefault(userId, sendAsEmail, defaultId).execute();
                    return defaultId;
                } catch (IOException e) {
                    System.err.printf("An error occured during setDefault: %s", e);
                }
            }
        } else {
            return defaultCertId;
        }

        return null;
    }
}
// [END gmail_update_smime_certs]