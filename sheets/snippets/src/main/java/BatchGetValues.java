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


// [START sheets_batch_get_values]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Spreadsheet Batch Get Values API */
public class BatchGetValues {
    /**
     * Returns one or more ranges of values from a spreadsheet.
     *
     * @param spreadsheetId - Id of the spreadsheet.
     * @param ranges - Range of cells of the spreadsheet.
     * @return Values in the range
     * @throws IOException - if credentials file not found.
     */
    public static BatchGetValuesResponse batchGetValues(String spreadsheetId,
                                                        List<String> ranges)
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

        BatchGetValuesResponse result = null;
        try {
            // Gets the values of the cells in the specified range.
            result = service.spreadsheets().values().batchGet(spreadsheetId)
                    .setRanges(ranges).execute() ;
            System.out.printf("%d ranges retrieved.", result.getValueRanges().size());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Spreadsheet not found with id '%s'.\n",spreadsheetId);
            } else {
                throw e;
            }
        }
        return result;
    }
}
// [END sheets_batch_get_values]