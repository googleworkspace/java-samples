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


// [START sheets_append_values]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Spreadsheet Append Values API */
public class AppendValues {
    /**
     * Appends values to a spreadsheet.
     *
     * @param spreadsheetId - Id of the spreadsheet.
     * @param range - Range of cells of the spreadsheet.
     * @param valueInputOption - Determines how input data should be interpreted.
     * @param values - list of rows of values to input.
     * @return spreadsheet with appended values
     * @throws IOException - if credentials file not found.
     */
    public static AppendValuesResponse appendValues(String spreadsheetId,
                                                    String range,
                                                    String valueInputOption,
                                                    List<List<Object>> values)
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

        AppendValuesResponse result = null;
        try {
            // Append values to the specified range.
            ValueRange body = new ValueRange()
                    .setValues(values);
            result = service.spreadsheets().values().append(spreadsheetId, range, body)
                    .setValueInputOption(valueInputOption)
                    .execute();
            // Prints the spreadsheet with appended values.
            System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
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
// [END sheets_append_values]