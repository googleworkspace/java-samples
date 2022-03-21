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


// [START sheets_batch_update]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.FindReplaceRequest;
import com.google.api.services.sheets.v4.model.FindReplaceResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Spreadsheet Batch Update API */
public class BatchUpdate {
    /**
     * Updates spreadsheet's title and cell values.
     *
     * @param spreadsheetId - Id of the spreadsheet.
     * @param title - New title of the spreadsheet.
     * @param find - Find cell values
     * @param replacement - Replaced cell values
     * @return response metadata
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdateSpreadsheetResponse batchUpdate(String spreadsheetId,
                                                             String title,
                                                             String find,
                                                             String replacement)
            throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the sheets API client
        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Sheets samples")
                .build();

        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetResponse response = null;
        try {
            // Change the spreadsheet's title.
            requests.add(new Request()
                    .setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
                            .setProperties(new SpreadsheetProperties()
                                    .setTitle(title))
                            .setFields("title")));
            // Find and replace text.
            requests.add(new Request()
                    .setFindReplace(new FindReplaceRequest()
                            .setFind(find)
                            .setReplacement(replacement)
                            .setAllSheets(true)));

            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            response = service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
            FindReplaceResponse findReplaceResponse = response.getReplies().get(1).getFindReplace();

            System.out.printf("%d replacements made.", findReplaceResponse.getOccurrencesChanged());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Spreadsheet not found with id '%s'.\n",spreadsheetId);
            } else {
                throw e;
            }
        }
        return response;
    }
}
// [END sheets_batch_update]