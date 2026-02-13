/ *
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
* /

// [START sheets_filter_views]

/*
 * Dependencies (Maven):
 * com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0
 * com.google.auth:google-auth-library-oauth2-http:1.19.0
 */

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class SheetsFilterViews {

    public static void main(String... args) {
        filterViews("1CM29gwKIzeXsAppeNwrc8lbYaVMmUclprLuLYuHog4k");
    }

    public static void filterViews(String spreadsheetId) {
        try {
            // Load pre-authorized user credentials from the environment.
            // TODO(developer) - See https://developers.google.com/identity for guides on implementing OAuth2.
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
            
            Sheets service = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Sheets Filter Views Sample")
                    .build();

            // --- Step 1: Add Filter View ---
            GridRange myRange = new GridRange()
                    .setSheetId(0)
                    .setStartRowIndex(0)
                    .setStartColumnIndex(0);

            // Construct Criteria for Column 0 (Hidden Values)
            FilterCriteria criteria0 = new FilterCriteria()
                    .setHiddenValues(Collections.singletonList("Panel"));

            // Construct Criteria for Column 6 (Date Condition)
            ConditionValue dateValue = new ConditionValue().setUserEnteredValue("4/30/2016");
            BooleanCondition dateCondition = new BooleanCondition()
                    .setType("DATE_BEFORE")
                    .setValues(Collections.singletonList(dateValue));
            FilterCriteria criteria6 = new FilterCriteria().setCondition(dateCondition);

            // Map criteria to column indices (Note: keys are Strings in Java map)
            Map<String, FilterCriteria> criteriaMap = new HashMap<>();
            criteriaMap.put("0", criteria0);
            criteriaMap.put("6", criteria6);

            FilterView filterView = new FilterView()
                    .setTitle("Sample Filter")
                    .setRange(myRange)
                    .setSortSpecs(Collections.singletonList(
                            new SortSpec().setDimensionIndex(3).setSortOrder("DESCENDING")
                    ))
                    .setCriteria(criteriaMap);

            AddFilterViewRequest addFilterViewRequest = new AddFilterViewRequest().setFilter(filterView);

            BatchUpdateSpreadsheetRequest batchRequest1 = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(new Request().setAddFilterView(addFilterViewRequest)));

            BatchUpdateSpreadsheetResponse response1 = service.spreadsheets()
                    .batchUpdate(spreadsheetId, batchRequest1)
                    .execute();

            // --- Step 2: Duplicate Filter View ---
            // Extract the ID from the previous response
            int filterId = response1.getReplies().get(0)
                    .getAddFilterView().getFilter().getFilterViewId();

            DuplicateFilterViewRequest duplicateRequest = new DuplicateFilterViewRequest()
                    .setFilterId(filterId);

            BatchUpdateSpreadsheetRequest batchRequest2 = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(new Request().setDuplicateFilterView(duplicateRequest)));

            BatchUpdateSpreadsheetResponse response2 = service.spreadsheets()
                    .batchUpdate(spreadsheetId, batchRequest2)
                    .execute();

            // --- Step 3: Update Filter View ---
            // Extract the new ID from the duplicate response
            int newFilterId = response2.getReplies().get(0)
                    .getDuplicateFilterView().getFilter().getFilterViewId();

            // Create update criteria
            Map<String, FilterCriteria> updateCriteriaMap = new HashMap<>();
            updateCriteriaMap.put("0", new FilterCriteria()); // Empty criteria
            
            ConditionValue numValue = new ConditionValue().setUserEnteredValue("5");
            BooleanCondition numCondition = new BooleanCondition()
                    .setType("NUMBER_GREATER")
                    .setValues(Collections.singletonList(numValue));
            updateCriteriaMap.put("3", new FilterCriteria().setCondition(numCondition));

            FilterView updateFilterView = new FilterView()
                    .setFilterViewId(newFilterId)
                    .setTitle("Updated Filter")
                    .setCriteria(updateCriteriaMap);

            UpdateFilterViewRequest updateRequest = new UpdateFilterViewRequest()
                    .setFilter(updateFilterView)
                    .setFields("criteria,title");

            BatchUpdateSpreadsheetRequest batchRequest3 = new BatchUpdateSpreadsheetRequest()
                    .setRequests(Collections.singletonList(new Request().setUpdateFilterView(updateRequest)));

            BatchUpdateSpreadsheetResponse response3 = service.spreadsheets()
                    .batchUpdate(spreadsheetId, batchRequest3)
                    .execute();

            System.out.println(response3.toPrettyString());

        } catch (IOException | GeneralSecurityException e) {
            System.err.println("An error occurred: " + e);
        }
    }
}

// [END sheets_filter_views]
