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


// [START slides_text_merging]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.ReplaceAllTextRequest;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.Response;
import com.google.api.services.slides.v1.model.SubstringMatchCriteria;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate the use of Slides Text Merging API */
public class TextMerging {
    /**
     * Changes specified texts with data from spreadsheet.
     *
     * @param templatePresentationId - id of the presentation.
     * @param dataSpreadsheetId - id of the spreadsheet containing data.
     * @return merged presentation id
     * @throws IOException - if credentials file not found.
     */
    public static List<BatchUpdatePresentationResponse> textMerging(
            String templatePresentationId, String dataSpreadsheetId) throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Arrays.asList(SlidesScopes.PRESENTATIONS,
                        SlidesScopes.DRIVE,SlidesScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the slides API client
        Slides service = new Slides.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        // Create the drive API client
        Drive driveService = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        // Create the sheets API client
        Sheets sheetsService = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        List<BatchUpdatePresentationResponse> responses = new ArrayList<>(5);
        // Use the Sheets API to load data, one record per row.
        String dataRangeNotation = "Customers!A2:M6";
        ValueRange sheetsResponse = sheetsService.spreadsheets().values()
                .get(dataSpreadsheetId, dataRangeNotation).execute();
        List<List<Object>> values = sheetsResponse.getValues();

        try {
            // For each record, create a new merged presentation.
            for (List<Object> row : values) {
                String customerName = row.get(2).toString();     // name in column 3
                String caseDescription = row.get(5).toString();  // case description in column 6
                String totalPortfolio = row.get(11).toString();  // total portfolio in column 12

                // Duplicate the template presentation using the Drive API.
                String copyTitle = customerName + " presentation";
                File content = new File().setName(copyTitle);
                File presentationFile =
                        driveService.files().copy(templatePresentationId, content).execute();
                String presentationId = presentationFile.getId();

                // Create the text merge (replaceAllText) requests for this presentation.
                List<Request> requests = new ArrayList<>();
                requests.add(new Request()
                        .setReplaceAllText(new ReplaceAllTextRequest()
                                .setContainsText(new SubstringMatchCriteria()
                                        .setText("{{customer-name}}")
                                        .setMatchCase(true))
                                .setReplaceText(customerName)));
                requests.add(new Request()
                        .setReplaceAllText(new ReplaceAllTextRequest()
                                .setContainsText(new SubstringMatchCriteria()
                                        .setText("{{case-description}}")
                                        .setMatchCase(true))
                                .setReplaceText(caseDescription)));
                requests.add(new Request()
                        .setReplaceAllText(new ReplaceAllTextRequest()
                                .setContainsText(new SubstringMatchCriteria()
                                        .setText("{{total-portfolio}}")
                                        .setMatchCase(true))
                                .setReplaceText(totalPortfolio)));

                // Execute the requests for this presentation.
                BatchUpdatePresentationRequest body =
                        new BatchUpdatePresentationRequest().setRequests(requests);
                BatchUpdatePresentationResponse response = service.presentations().batchUpdate(presentationId, body).execute();

                // Count total number of replacements made.
                int numReplacements = 0;
                for (Response resp : response.getReplies()) {
                    numReplacements += resp.getReplaceAllText().getOccurrencesChanged();
                }
                // Prints the merged presentation id and count of replacements.
                System.out.println("Created merged presentation for " +
                        customerName + " with ID: " + presentationId);
                System.out.println("Replaced " + numReplacements + " text instances.");
            }
        }catch (NullPointerException ne) {
            System.out.println("Text not found to replace with image.");
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Presentation not found with id '%s'.\n", templatePresentationId);
            } else {
                throw e;
            }
        }
        return responses;
    }
}
// [END slides_text_merging]