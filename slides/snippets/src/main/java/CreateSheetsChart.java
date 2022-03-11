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


// [START slides_create_sheets_chart]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.AffineTransform;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.CreateSheetsChartRequest;
import com.google.api.services.slides.v1.model.Dimension;
import com.google.api.services.slides.v1.model.PageElementProperties;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.Size;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Slides Create Chart API */
public class CreateSheetsChart {
    /**
     * Adds chart from spreadsheet to slides as linked.
     *
     * @param presentationId - id of the presentation.
     * @param pageId - id of the page.
     * @param spreadsheetId - id of the spreadsheet.
     * @param sheetChartId - id of the chart in sheets.
     * @return presentation chart id
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdatePresentationResponse createSheetsChart(
            String presentationId, String pageId, String spreadsheetId, Integer sheetChartId)
            throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(SlidesScopes.PRESENTATIONS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the slides API client
        Slides service = new Slides.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        // Embed a Sheets chart (indicated by the spreadsheetId and sheetChartId) onto
        // a page in the presentation. Setting the linking mode as "LINKED" allows the
        // chart to be refreshed if the Sheets version is updated.
        List<Request> requests = new ArrayList<>();
        Dimension emu4M = new Dimension().setMagnitude(4000000.0).setUnit("EMU");
        String presentationChartId = "MyEmbeddedChart";
        requests.add(new Request()
                .setCreateSheetsChart(new CreateSheetsChartRequest()
                        .setObjectId(presentationChartId)
                        .setSpreadsheetId(spreadsheetId)
                        .setChartId(sheetChartId)
                        .setLinkingMode("LINKED")
                        .setElementProperties(new PageElementProperties()
                                .setPageObjectId(pageId)
                                .setSize(new Size()
                                        .setHeight(emu4M)
                                        .setWidth(emu4M))
                                .setTransform(new AffineTransform()
                                        .setScaleX(1.0)
                                        .setScaleY(1.0)
                                        .setTranslateX(100000.0)
                                        .setTranslateY(100000.0)
                                        .setUnit("EMU")))));

        BatchUpdatePresentationResponse response = null;
        try {
            // Execute the request.
            BatchUpdatePresentationRequest body =
                    new BatchUpdatePresentationRequest().setRequests(requests);
            response = service.presentations().batchUpdate(presentationId, body).execute();
            System.out.println("Added a linked Sheets chart with ID " + presentationChartId);
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Presentation not found with id '%s'.\n", presentationId);
            }
            else {
                throw e;
            }
        }
        return response;
    }
}
// [END slides_create_sheets_chart]