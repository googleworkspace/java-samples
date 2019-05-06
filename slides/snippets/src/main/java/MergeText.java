/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// [START slides_text_merging]
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeText {

    // Create a new presentation
    public static void mergeText(String templatePresentationId, String spreadsheetId) {
        try {
            // String templatePresentationId = "55BYRgExa5NEv9vMegpPBaWESy7oKTK21l4P2DcX5l4";
            // String spreadsheetId = "1n6LkAHJ-rTflmy7L-BJil5KVKN3eECqrO3V1quv1QLk";

            // Load user credentials from environment.
            // See <some URL> for additional ways to acquire credentials for
            // your application type and use case (CLI, web application, etc.)
            List<String> scopes = Arrays.asList(SlidesScopes.PRESENTATIONS);
            GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(scopes);

            // Build the API client
            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
            String applicationName = "Slides API Samples";
            Drive drive = new Drive.Builder(
                    transport,
                    jacksonFactory,
                    credential)
                    .setApplicationName(applicationName)
                    .build();
            Sheets sheets = new Sheets.Builder(
                    transport,
                    jacksonFactory,
                    credential)
                    .setApplicationName(applicationName)
                    .build();
            Slides slides = new Slides.Builder(
                    transport,
                    jacksonFactory,
                    credential)
                    .setApplicationName(applicationName)
                    .build();

            // Use the Sheets API to load data, one record per row.
            String dataRangeNotation = "Customers!A2:M6";
            ValueRange sheetsResponse = sheets.spreadsheets().values()
                    .get(spreadsheetId, dataRangeNotation).execute();
            List<List<Object>> rows = sheetsResponse.getValues();

            // For each record, create a new merged presentation.
            for (List<Object> row : rows) {
                String customerName = row.get(2).toString();     // name in column 3
                String caseDescription = row.get(5).toString();  // case description in column 6
                String totalPortfolio = row.get(11).toString();  // total portfolio in column 12

                // Duplicate the template presentation using the Drive API.
                String copyTitle = customerName + " presentation";
                File content = new File().setName(copyTitle);
                File presentationFile = drive.files().copy(templatePresentationId, content).execute();
                String presentationId = presentationFile.getId();

                // Create the text merge (replaceAllText) requests for this presentation.
                List<Request> requests = new ArrayList<>();
                SubstringMatchCriteria customerMatch = new SubstringMatchCriteria()
                        .setText("{{customer-name}}")
                        .setMatchCase(true);
                ReplaceAllTextRequest replaceCustomerName = new ReplaceAllTextRequest()
                        .setContainsText(customerMatch)
                        .setReplaceText(customerName);
                requests.add(new Request().setReplaceAllText(replaceCustomerName));
                SubstringMatchCriteria caseMatch = new SubstringMatchCriteria()
                        .setText("{{case-description}}")
                        .setMatchCase(true);
                ReplaceAllTextRequest replaceDescription = new ReplaceAllTextRequest()
                        .setContainsText(caseMatch)
                        .setReplaceText(caseDescription);
                requests.add(new Request().setReplaceAllText(replaceDescription));
                SubstringMatchCriteria portfolioMatch = new SubstringMatchCriteria()
                        .setText("{{total-portfolio}}")
                        .setMatchCase(true);
                ReplaceAllTextRequest replacePortfolioValue = new ReplaceAllTextRequest()
                        .setContainsText(portfolioMatch)
                        .setReplaceText(totalPortfolio);
                requests.add(new Request().setReplaceAllText(replacePortfolioValue));

                // Execute the requests for this presentation.
                BatchUpdatePresentationRequest body =
                        new BatchUpdatePresentationRequest().setRequests(requests);
                BatchUpdatePresentationResponse response =
                        slides.presentations().batchUpdate(presentationId, body).execute();
                // Count total number of replacements made.
                int numReplacements = 0;
                for (Response resp : response.getReplies()) {
                    numReplacements += resp.getReplaceAllText().getOccurrencesChanged();
                }

                System.out.println("Created merged presentation for " +
                        customerName + " with ID: " + presentationId);
                System.out.println("Replaced " + numReplacements + " text instances.");
            }
        } catch (Exception e) {
            System.out.println("Error during createSlide: \n" + e.toString());
        }
    }
}
// [END slides_text_merging]
