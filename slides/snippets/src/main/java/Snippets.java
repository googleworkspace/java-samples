import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Snippets {

    private Slides service;
    private Drive driveService;
    private Sheets sheetsService;

    Snippets(Slides service, Drive driveService, Sheets sheetsService) {
        this.service = service;
        this.driveService = driveService;
        this.sheetsService = sheetsService;
    }

    public String createPresentation(String title) throws IOException {
        Slides slidesService = this.service;
        // [START slides_create_presentation]
        Presentation presentation = new Presentation()
                .setTitle(title);
        presentation = slidesService.presentations().create(presentation)
                .setFields("presentationId")
                .execute();
        System.out.println("Created presentation with ID: " + presentation.getPresentationId());
        // [END slides_create_presentation]
        return presentation.getPresentationId();
    }

    public String copyPresentation(String presentationId, String copyTitle) throws IOException {
        Drive driveService = this.driveService;
        // [START slides_copy_presentation]
        File copyMetadata = new File().setName(copyTitle);
        File presentationCopyFile =
                driveService.files().copy(presentationId, copyMetadata).execute();
        String presentationCopyId = presentationCopyFile.getId();
        // [END slides_copy_presentation]
        return presentationCopyId;
    }

    public BatchUpdatePresentationResponse createSlide(String presentationId) throws IOException {
        Slides slidesService = this.service;
        // [START slides_create_slide]
        // Add a slide at index 1 using the predefined "TITLE_AND_TWO_COLUMNS" layout
        // and the ID "MyNewSlide_001".
        List<Request> requests = new ArrayList<>();
        String slideId = "MyNewSlide_001";
        requests.add(new Request()
                .setCreateSlide(new CreateSlideRequest()
                        .setObjectId(slideId)
                        .setInsertionIndex(1)
                        .setSlideLayoutReference(new LayoutReference()
                                .setPredefinedLayout("TITLE_AND_TWO_COLUMNS"))));

        // If you wish to populate the slide with elements, add create requests here,
        // using the slide ID specified above.

        // Execute the request.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();
        CreateSlideResponse createSlideResponse = response.getReplies().get(0).getCreateSlide();
        System.out.println("Created slide with ID: " + createSlideResponse.getObjectId());
        // [END slides_create_slide]
        return response;
    }

    public BatchUpdatePresentationResponse createTextBoxWithText(
            String presentationId, String slideId) throws IOException {
        Slides slidesService = this.service;
        // [START slides_create_textbox_with_text]
        // Create a new square text box, using a supplied object ID.
        List<Request> requests = new ArrayList<>();
        String textBoxId = "MyTextBox_01";
        Dimension pt350 = new Dimension().setMagnitude(350.0).setUnit("PT");
        requests.add(new Request()
                .setCreateShape(new CreateShapeRequest()
                        .setObjectId(textBoxId)
                        .setShapeType("TEXT_BOX")
                        .setElementProperties(new PageElementProperties()
                                .setPageObjectId(slideId)
                                .setSize(new Size()
                                        .setHeight(pt350)
                                        .setWidth(pt350))
                                .setTransform(new AffineTransform()
                                        .setScaleX(1.0)
                                        .setScaleY(1.0)
                                        .setTranslateX(350.0)
                                        .setTranslateY(100.0)
                                        .setUnit("PT")))));

        // Insert text into the box, using the object ID given to it.
        requests.add(new Request()
                .setInsertText(new InsertTextRequest()
                        .setObjectId(textBoxId)
                        .setInsertionIndex(0)
                        .setText("New Box Text Inserted")));

        // Execute the requests.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();
        CreateShapeResponse createShapeResponse = response.getReplies().get(0).getCreateShape();
        System.out.println("Created textbox with ID: " + createShapeResponse.getObjectId());
        // [END slides_create_textbox_with_text]
        return response;
    }


    public BatchUpdatePresentationResponse createImage(String presentationId,
                                                       String slideId,
                                                       String imageFilePath,
                                                       String imageMimeType,
                                                       GoogleCredential credential)
            throws IOException {
        Slides slidesService = this.service;
        Drive driveService = this.driveService;
        // [START slides_create_image]
        // Temporarily upload a local image file to Drive, in order to to obtain a URL
        // for the image. Alternatively, you can provide the Slides service a URL of
        // an already hosted image.
        File file = new File();
        file.setName("My Image File");
        FileContent mediaContent = new FileContent(imageMimeType, new java.io.File(imageFilePath));
        File uploadedFile = driveService.files().create(file, mediaContent).execute();
        String fileId = uploadedFile.getId();

        // Obtain a URL for the image.
        GenericUrl getFileUrlBuilder = driveService.files().get(fileId).buildHttpRequestUrl();
        String imageUrl = getFileUrlBuilder
                .set("access_token", credential.getAccessToken())
                .set("alt", "media").build();

        // Create a new image, using a supplied object ID, with content downloaded from imageUrl.
        List<Request> requests = new ArrayList<>();
        String imageId = "MyImageId_01";
        Dimension emu4M = new Dimension().setMagnitude(4000000.0).setUnit("EMU");
        requests.add(new Request()
                .setCreateImage(new CreateImageRequest()
                        .setObjectId(imageId)
                        .setUrl(imageUrl)
                        .setElementProperties(new PageElementProperties()
                                .setPageObjectId(slideId)
                                .setSize(new Size()
                                        .setHeight(emu4M)
                                        .setWidth(emu4M))
                                .setTransform(new AffineTransform()
                                        .setScaleX(1.0)
                                        .setScaleY(1.0)
                                        .setTranslateX(100000.0)
                                        .setTranslateY(100000.0)
                                        .setUnit("EMU")))));

        // Execute the request.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();
        CreateImageResponse createImageResponse = response.getReplies().get(0).getCreateImage();
        System.out.println("Created image with ID: " + createImageResponse.getObjectId());

        // Remove the temporary image file from Drive.
        driveService.files().delete(fileId).execute();
        // [END slides_create_image]
        return response;
    }

    public List<BatchUpdatePresentationResponse> textMerging(
            String templatePresentationId, String dataSpreadsheetId) throws IOException {
        Slides slidesService = this.service;
        Drive driveService = this.driveService;
        Sheets sheetsService = this.sheetsService;
        List<BatchUpdatePresentationResponse> responses = new ArrayList<>(5);
        // [START slides_text_merging]
        // Use the Sheets API to load data, one record per row.
        String dataRangeNotation = "Customers!A2:M6";
        ValueRange sheetsResponse = sheetsService.spreadsheets().values()
                .get(dataSpreadsheetId, dataRangeNotation).execute();
        List<List<Object>> values = sheetsResponse.getValues();

        // For each record, create a new merged presentation.
        for (List<Object> row: values) {
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
            BatchUpdatePresentationResponse response =
                    slidesService.presentations().batchUpdate(presentationId, body).execute();
            // [START_EXCLUDE silent]
            responses.add(response);
            // [END_EXCLUDE]
            // Count total number of replacements made.
            int numReplacements = 0;
            for (Response resp : response.getReplies()) {
                numReplacements += resp.getReplaceAllText().getOccurrencesChanged();
            }

            System.out.println("Created merged presentation for " +
                    customerName + " with ID: " + presentationId);
            System.out.println("Replaced " + numReplacements + " text instances.");
        }
        // [END slides_text_merging]
        return responses;
    }

    public BatchUpdatePresentationResponse imageMerging(String templatePresentationId,
                                                        String imageUrl,
                                                        String customerName) throws IOException {
        Slides slidesService = this.service;
        Drive driveService = this.driveService;
        String logoUrl = imageUrl;
        String customerGraphicUrl = imageUrl;

        // [START slides_image_merging]
        // Duplicate the template presentation using the Drive API.
        String copyTitle = customerName + " presentation";
        File content = new File().setName(copyTitle);
        File presentationFile =
                driveService.files().copy(templatePresentationId, content).execute();
        String presentationId = presentationFile.getId();

        // Create the image merge (replaceAllShapesWithImage) requests.
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setReplaceAllShapesWithImage(new ReplaceAllShapesWithImageRequest()
                        .setImageUrl(logoUrl)
                        .setReplaceMethod("CENTER_INSIDE")
                        .setContainsText(new SubstringMatchCriteria()
                                .setText("{{company-logo}}")
                                .setMatchCase(true))));
        requests.add(new Request()
                .setReplaceAllShapesWithImage(new ReplaceAllShapesWithImageRequest()
                        .setImageUrl(customerGraphicUrl)
                        .setReplaceMethod("CENTER_INSIDE")
                        .setContainsText(new SubstringMatchCriteria()
                                .setText("{{customer-graphic}}")
                                .setMatchCase(true))));

        // Execute the requests.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();

        // Count total number of replacements made.
        int numReplacements = 0;
        for(Response resp: response.getReplies()) {
            numReplacements += resp.getReplaceAllShapesWithImage().getOccurrencesChanged();
        }

        System.out.println("Created merged presentation with ID: " + presentationId);
        System.out.println("Replaced " + numReplacements + " shapes instances with images.");
        // [END slides_image_merging]
        return response;
    }

    public BatchUpdatePresentationResponse simpleTextReplace(
            String presentationId, String shapeId, String replacementText) throws IOException {
        Slides slidesService = this.service;
        // [START slides_simple_text_replace]
        // Remove existing text in the shape, then insert the new text.
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setDeleteText(new DeleteTextRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                            .setType("ALL"))));
        requests.add(new Request()
                .setInsertText(new InsertTextRequest()
                        .setObjectId(shapeId)
                        .setInsertionIndex(0)
                        .setText(replacementText)));

        // Execute the requests.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();

        System.out.println("Replaced text in shape with ID: " + shapeId);
        // [END slides_simple_text_replace]
        return response;
    }

    public BatchUpdatePresentationResponse textStyleUpdate(String presentationId, String shapeId)
            throws IOException {
        Slides slidesService = this.service;
        // [START slides_text_style_update]
        // Update the text style so that the first 5 characters are bolded
        // and italicized, and the next 5 are displayed in blue 14 pt Times
        // New Roman font, and the next five are hyperlinked.
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("FIXED_RANGE")
                                .setStartIndex(0)
                                .setEndIndex(5))
                        .setStyle(new TextStyle()
                                .setBold(true)
                                .setItalic(true))
                        .setFields("bold,italic")));
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("FIXED_RANGE")
                                .setStartIndex(5)
                                .setEndIndex(10))
                        .setStyle(new TextStyle()
                                .setFontFamily("Times New Roman")
                                .setFontSize(new Dimension()
                                        .setMagnitude(14.0)
                                        .setUnit("PT"))
                                .setForegroundColor(new OptionalColor()
                                        .setOpaqueColor(new OpaqueColor()
                                                .setRgbColor(new RgbColor()
                                                        .setBlue(1.0F)
                                                        .setGreen(0.0F)
                                                        .setRed(0.0F)))))
                        .setFields("foregroundColor,fontFamily,fontSize")));
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("FIXED_RANGE")
                                .setStartIndex(10)
                                .setEndIndex(15))
                        .setStyle(new TextStyle()
                                .setLink(new Link()
                                        .setUrl("www.example.com")))
                        .setFields("link")));

        // Execute the requests.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();

        System.out.println("Updated text style for shape with ID: " + shapeId);
        // [END slides_text_style_update]
        return response;
    }

    public BatchUpdatePresentationResponse createBulletedText(String presentationId,
                                                              String shapeId) throws IOException {
        Slides slidesService = this.service;
        // [START slides_create_bulleted_text]
        // Add arrow-diamond-disc bullets to all text in the shape.
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setCreateParagraphBullets(new CreateParagraphBulletsRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("ALL"))
                        .setBulletPreset("BULLET_ARROW_DIAMOND_DISC")));

        // Execute the request.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();
        System.out.println("Added bullets to text in shape with ID: " + shapeId);
        // [END slides_create_bulleted_text]
        return response;
    }

    public BatchUpdatePresentationResponse createSheetsChart(
            String presentationId, String pageId, String spreadsheetId, Integer sheetChartId)
            throws IOException {
        Slides slidesService = this.service;
        // [START slides_create_sheets_chart]
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

        // Execute the request.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();
        System.out.println("Added a linked Sheets chart with ID " + presentationChartId);
        // [END slides_create_sheets_chart]
        return response;
    }

    public BatchUpdatePresentationResponse refreshSheetsChart(
            String presentationId, String presentationChartId) throws IOException {
        Slides slidesService = this.service;
        // [START slides_refresh_sheets_chart]
        List<Request> requests = new ArrayList<>();

        // Refresh an existing linked Sheets chart embedded a presentation.
        requests.add(new Request()
                .setRefreshSheetsChart(new RefreshSheetsChartRequest()
                        .setObjectId(presentationChartId)));

        // Execute the request.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                slidesService.presentations().batchUpdate(presentationId, body).execute();
        System.out.println("Refreshed a linked Sheets chart with ID " + presentationChartId);
        // [END slides_refresh_sheets_chart]
        return response;
    }
}
