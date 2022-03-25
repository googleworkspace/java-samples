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

// [START apps_script_api_quickstart]

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.script.Script;
import com.google.api.services.script.model.Content;
import com.google.api.services.script.model.CreateProjectRequest;
import com.google.api.services.script.model.File;
import com.google.api.services.script.model.Project;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class AppsScriptQuickstart {
    private static final String APPLICATION_NAME = "Apps Script API Java Quickstart";


    public static void main(String... args) throws IOException, GeneralSecurityException {
        /*Load pre-authorized user credentials from the environment.
        TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList("https://www.googleapis.com/auth/script.projects"));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        com.google.api.services.script.Script service = new com.google.api.services.script.Script.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
        Script.Projects projects = service.projects();

        // Creates a new script project.
        Project createOp = projects.create(new CreateProjectRequest().setTitle("My Script")).execute();

        // Uploads two files to the project.
        File file1 = new File()
                .setName("hello")
                .setType("SERVER_JS")
                .setSource("function helloWorld() {\n  console.log(\"Hello, world!\");\n}");
        File file2 = new File()
                .setName("appsscript")
                .setType("JSON")
                .setSource("{\"timeZone\":\"America/New_York\",\"exceptionLogging\":\"CLOUD\"}");
        Content content = new Content().setFiles(Arrays.asList(file1, file2));
        Content updatedContent = projects.updateContent(createOp.getScriptId(), content).execute();

        // Logs the project URL.
        System.out.printf("https://script.google.com/d/%s/edit\n", updatedContent.getScriptId());
    }
}
// [END apps_script_api_quickstart]
