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

// [START vault_quickstart]

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vault.v1.VaultScopes;
import com.google.api.services.vault.v1.model.*;
import com.google.api.services.vault.v1.Vault;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Quickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Vault API Java Quickstart";


    /**
     * Build and return an authorized Vault client service.
     * @return an authorized Vault client service
     * @throws IOException
     */
    public static Vault getVaultService() throws IOException {
        /*Load pre-authorized user credentials from the environment.
        TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(VaultScopes.EDISCOVERY_READONLY));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        com.google.api.services.vault.v1.Vault service = new com.google.api.services.vault.v1.Vault.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Vault service = getVaultService();

        // List the first 10 matters.
        ListMattersResponse response = service.matters().list()
            .setPageSize(10)
            .execute();
        List<Matter> matters = response.getMatters();
        if (matters == null || matters.size() == 0) {
            System.out.println("No matters found.");
        } else {
            System.out.println("Matters:");
            for (Matter matter: matters) {
                System.out.printf("%s (%s)\n", matter.getName(),
                        matter.getMatterId());
            }
        }
    }
}
// [END vault_quickstart]
