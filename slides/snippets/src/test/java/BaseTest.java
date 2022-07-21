/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.Before;
import org.junit.After;

public class BaseTest {
    protected Slides service;
    protected Drive driveService;
    protected Sheets sheetsService;

    public GoogleCredentials getCredential() throws IOException {
    /* Load pre-authorized user credentials from the environment.
     TODO(developer) - See https://developers.google.com/identity for
      guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(SlidesScopes.PRESENTATIONS, SlidesScopes.DRIVE);
        return credentials;
    }

    public Slides buildService(GoogleCredentials credential) {
        return new Slides.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credential))
                .setApplicationName("Slides API Snippets")
                .build();
    }

    public Drive buildDriveService(GoogleCredentials credential) {
        return new Drive.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credential))
                .setApplicationName("Slides API Snippets")
                .build();
    }

    public Sheets buildSheetsService(GoogleCredentials credential) {
        return new Sheets.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credential))
                .setApplicationName("Slides API Snippets")
                .build();
    }

    @Before
    public void setup() throws IOException {
        GoogleCredentials credential = getCredential();
        this.service = buildService(credential);
        this.driveService = buildDriveService(credential);
        this.sheetsService = buildSheetsService(credential);
    }

    protected void deleteFileOnCleanup(String id) throws IOException {
        this.driveService.files().delete(id).execute();
    }

    protected String createTestPresentation() throws IOException {
        Presentation presentation = new Presentation()
                .setTitle("Test Presentation");
        presentation = service.presentations().create(presentation)
                .setFields("presentationId")
                .execute();
        return presentation.getPresentationId();
    }

    protected String createTestSlide(String presentationId) throws IOException {
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setCreateSlide(new CreateSlideRequest()
                        .setObjectId("TestSlide")
                        .setInsertionIndex(0)
                        .setSlideLayoutReference(new LayoutReference()
                                .setPredefinedLayout("BLANK"))));
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                service.presentations().batchUpdate(presentationId, body).execute();
        return response.getReplies().get(0).getCreateSlide().getObjectId();
    }

    protected String createTestTextBox(String presentationId, String pageId) throws IOException {
        String textBoxId = "MyTextBox_01";
        Dimension pt350 = new Dimension().setMagnitude(350.0).setUnit("PT");
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setCreateShape(new CreateShapeRequest()
                        .setObjectId(textBoxId)
                        .setShapeType("TEXT_BOX")
                        .setElementProperties(new PageElementProperties()
                                .setPageObjectId(pageId)
                                .setSize(new Size()
                                        .setHeight(pt350)
                                        .setWidth(pt350))
                                .setTransform(new AffineTransform()
                                        .setScaleX(1.0)
                                        .setScaleY(1.0)
                                        .setTranslateX(350.0)
                                        .setTranslateY(100.0)
                                        .setUnit("PT")))));

        requests.add(new Request()
                .setInsertText(new InsertTextRequest()
                        .setObjectId(textBoxId)
                        .setInsertionIndex(0)
                        .setText("New Box Text Inserted")));

        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                service.presentations().batchUpdate(presentationId, body).execute();
        return response.getReplies().get(0).getCreateShape().getObjectId();
    }

    protected String createTestSheetsChart(String presentationId,
                                           String pageId,
                                           String spreadsheetId,
                                           Integer sheetChartId) throws IOException {
        String presentationChartId = "MyChartId_01";
        Dimension emu4M = new Dimension().setMagnitude(4000000.0).setUnit("EMU");
        List<Request> requests = new ArrayList<>();
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

        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                service.presentations().batchUpdate(presentationId, body).execute();
        return response.getReplies().get(0).getCreateSheetsChart().getObjectId();
    }

}
