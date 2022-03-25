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

// [START docs_quickstart]
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.Document;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Arrays;

/* class to demonstarte use of Docs get documents API */
public class DocsQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";

    private static final String DOCUMENT_ID = "195j9eDD3ccgjQRttHhJPymLJUCOUjs-jmwTrekvdjFE";

    public static void main(String... args) throws IOException{
        /*Load pre-authorized user credentials from the environment.
        TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DocsScopes.DOCUMENTS_READONLY));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        com.google.api.services.docs.v1.Docs service = new com.google.api.services.docs.v1.Docs.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Prints the title of the requested doc:
        // https://docs.google.com/document/d/195j9eDD3ccgjQRttHhJPymLJUCOUjs-jmwTrekvdjFE/edit
        Document response = service.documents().get(DOCUMENT_ID).execute();
        String title = response.getTitle();

        System.out.printf("The title of the doc is: %s\n", title);
    }
}
// [END docs_quickstart]
