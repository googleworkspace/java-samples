import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;

public class BaseTest {
    static {
        enableLogging();
    }

    protected GoogleCredential credential;
    protected Slides service;
    protected Drive driveService;
    protected Sheets sheetsService;
    protected Set<String> filesToDelete = new HashSet<String>();


    public static void enableLogging() {
        Logger logger = Logger.getLogger(HttpTransport.class.getName());
        logger.setLevel(Level.INFO);
        logger.addHandler(new Handler() {

            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(LogRecord record) {
                // default ConsoleHandler will print >= INFO to System.err
                if (record.getLevel().intValue() < Level.INFO.intValue()) {
                    System.out.println(record.getMessage());
                }
            }
        });
    }

    public GoogleCredential getCredential() throws IOException {
        return GoogleCredential.getApplicationDefault()
                .createScoped(Arrays.asList(DriveScopes.DRIVE));
    }

    public Slides buildService(GoogleCredential credential)
            throws IOException, GeneralSecurityException {
        return new Slides.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Slides API Snippets")
                .build();
    }

    public Drive buildDriveService(GoogleCredential credential)
            throws IOException, GeneralSecurityException {
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Slides API Snippets")
                .build();
    }

    public Sheets buildSheetsService(GoogleCredential credential)
            throws IOException, GeneralSecurityException {
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Slides API Snippets")
                .build();
    }

    @Before
    public void setup() throws IOException, GeneralSecurityException {
        this.credential = getCredential();
        this.service = buildService(credential);
        this.driveService = buildDriveService(credential);
        this.sheetsService = buildSheetsService(credential);
        this.filesToDelete.clear();
    }

    @After
    public void cleanupFiles() {
        for(String id : filesToDelete) {
            try {
                this.driveService.files().delete(id).execute();
            } catch (IOException e) {
                System.err.println("Unable to cleanup file " + id);
            }
        }
    }

    protected void deleteFileOnCleanup(String id) {
        filesToDelete.add(id);
    }

    protected String createTestPresentation() throws IOException {
        Presentation presentation = new Presentation()
                .setTitle("Test Presentation");
        presentation = service.presentations().create(presentation)
                .setFields("presentationId")
                .execute();
        String presentationId = presentation.getPresentationId();
        this.deleteFileOnCleanup(presentationId);
        return presentationId;
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
