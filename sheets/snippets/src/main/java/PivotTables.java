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


// [START sheets_pivot_tables]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.PivotGroup;
import com.google.api.services.sheets.v4.model.PivotTable;
import com.google.api.services.sheets.v4.model.PivotValue;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Spreadsheet Create Pivot Tables API */
public class PivotTables {
    /**
     * Create pivot table.
     *
     * @param spreadsheetId - Id of the spreadsheet.
     * @return pivot table's spreadsheet
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdateSpreadsheetResponse pivotTables(String spreadsheetId) throws IOException {
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

        // Create two sheets for our pivot table.
        List<Request> sheetsRequests = new ArrayList<>();
        BatchUpdateSpreadsheetResponse result = null;
        try {
            sheetsRequests.add(new Request().setAddSheet(new AddSheetRequest()));
            sheetsRequests.add(new Request().setAddSheet(new AddSheetRequest()));

            BatchUpdateSpreadsheetRequest createSheetsBody = new BatchUpdateSpreadsheetRequest()
                    .setRequests(sheetsRequests);
            BatchUpdateSpreadsheetResponse createSheetsResponse = service.spreadsheets()
                    .batchUpdate(spreadsheetId, createSheetsBody).execute();
            int sourceSheetId = createSheetsResponse.getReplies().get(0).getAddSheet().getProperties()
                    .getSheetId();
            int targetSheetId = createSheetsResponse.getReplies().get(1).getAddSheet().getProperties()
                    .getSheetId();

            PivotTable pivotTable = new PivotTable()
                    .setSource(
                            new GridRange()
                                    .setSheetId(sourceSheetId)
                                    .setStartRowIndex(0)
                                    .setStartColumnIndex(0)
                                    .setEndRowIndex(20)
                                    .setEndColumnIndex(7)
                    )
                    .setRows(Collections.singletonList(
                            new PivotGroup()
                                    .setSourceColumnOffset(1)
                                    .setShowTotals(true)
                                    .setSortOrder("ASCENDING")
                    ))
                    .setColumns(Collections.singletonList(
                            new PivotGroup()
                                    .setSourceColumnOffset(4)
                                    .setShowTotals(true)
                                    .setSortOrder("ASCENDING")
                    ))
                    .setValues(Collections.singletonList(
                            new PivotValue()
                                    .setSummarizeFunction("COUNTA")
                                    .setSourceColumnOffset(4)
                    ));
            List<Request> requests = Lists.newArrayList();
            Request updateCellsRequest = new Request().setUpdateCells(new UpdateCellsRequest()
                    .setFields("*")
                    .setRows(Collections.singletonList(
                            new RowData().setValues(
                                    Collections.singletonList(
                                            new CellData().setPivotTable(pivotTable))
                            )
                    ))
                    .setStart(new GridCoordinate()
                            .setSheetId(targetSheetId)
                            .setRowIndex(0)
                            .setColumnIndex(0)

                    ));

            requests.add(updateCellsRequest);
            BatchUpdateSpreadsheetRequest updateCellsBody = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            result = service.spreadsheets().batchUpdate(spreadsheetId, updateCellsBody).execute();
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
// [END sheets_pivot_tables]