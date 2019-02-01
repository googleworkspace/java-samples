import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.api.services.slides.v1.model.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SnippetsTest extends BaseTest {

    private Snippets snippets;

    private final String IMAGE_URL =
            "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";
    private final String IMAGE_FILE_PATH =
            "../images/googlelogo_color_272x92dp.png";
    private final String IMAGE_MIMETYPE = "image/png";
    private final String TEMPLATE_PRESENTATION_ID = "1wJUN1B5CQ2wQOBzmz2apky48QNK1OsE2oNKHPMLpKDc";
    private final String DATA_SPREADSHEET_ID = "14KaZMq2aCAGt5acV77zaA_Ps8aDt04G7T0ei4KiXLX8";
    private final Integer CHART_ID = 1107320627;

    private final String CUSTOMER_NAME = "Fake Customer";

    @Before
    public void createSnippets() {
        this.snippets = new Snippets(this.service, this.driveService, this.sheetsService);
    }

    @Test
    public void testCreatePresentation() throws IOException {
        String presentationId = this.snippets.createPresentation("Title");
        assertNotNull(presentationId);
        this.deleteFileOnCleanup(presentationId);
    }

    @Test
    public void testCopyPresentation() throws IOException {
        String presentationId = this.createTestPresentation();
        String copyId = this.snippets.copyPresentation(presentationId, "My Duplicate Presentation");
        assertNotNull(copyId);
        this.deleteFileOnCleanup(copyId);
    }

    @Test
    public void testCreateSlide() throws IOException {
        String presentationId = this.createTestPresentation();
        BatchUpdatePresentationResponse response = this.snippets.createSlide(presentationId);
        assertNotNull(response);
        assertEquals(1, response.getReplies().size());
        String pageId = response.getReplies().get(0).getCreateSlide().getObjectId();
        assertNotNull(pageId);
    }

    @Test
    public void testCreateTextBox() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        BatchUpdatePresentationResponse response =
                this.snippets.createTextBoxWithText(presentationId, pageId);
        assertEquals(2, response.getReplies().size());
        String boxId = response.getReplies().get(0).getCreateShape().getObjectId();
        assertNotNull(boxId);
    }

    @Test
    public void testCreateImage() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        BatchUpdatePresentationResponse response = this.snippets.createImage(
                presentationId, pageId, IMAGE_FILE_PATH, IMAGE_MIMETYPE, this.credential);
        assertEquals(1, response.getReplies().size());
        String imageId = response.getReplies().get(0).getCreateImage().getObjectId();
        assertNotNull(imageId);
    }

    @Test
    public void testTextMerge() throws IOException {
        List<BatchUpdatePresentationResponse> responses =
                this.snippets.textMerging(TEMPLATE_PRESENTATION_ID, DATA_SPREADSHEET_ID);
        for (BatchUpdatePresentationResponse response: responses) {
            String presentationId = response.getPresentationId();
            assertNotNull(presentationId);
            assertEquals(3, response.getReplies().size());
            int numReplacements = 0;
            for (Response resp : response.getReplies()) {
                numReplacements += resp.getReplaceAllText().getOccurrencesChanged();
            }
            assertEquals(4, numReplacements);
            this.deleteFileOnCleanup(presentationId);
        }
    }

    @Test
    public void testImageMerge() throws IOException {
        BatchUpdatePresentationResponse response =
                this.snippets.imageMerging(TEMPLATE_PRESENTATION_ID, IMAGE_URL, CUSTOMER_NAME);
        String presentationId = response.getPresentationId();
        assertNotNull(presentationId);
        assertEquals(2, response.getReplies().size());
        int numReplacements = 0;
        for(Response resp: response.getReplies()) {
            numReplacements += resp.getReplaceAllShapesWithImage().getOccurrencesChanged();
        }
        assertEquals(2, numReplacements);
        this.deleteFileOnCleanup(presentationId);
    }

    @Test
    public void testSimpleTextReplace() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        String boxId = this.createTestTextBox(presentationId, pageId);
        BatchUpdatePresentationResponse response =
                this.snippets.simpleTextReplace(presentationId, boxId, "MY NEW TEXT");
        assertEquals(2, response.getReplies().size());
    }

    @Test
    public void testTextStyleUpdate() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        String boxId = this.createTestTextBox(presentationId, pageId);
        BatchUpdatePresentationResponse response =
                this.snippets.textStyleUpdate(presentationId, boxId);
        assertEquals(3, response.getReplies().size());
    }

    @Test
    public void testCreateBulletText() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        String boxId = this.createTestTextBox(presentationId, pageId);
        BatchUpdatePresentationResponse response =
                this.snippets.createBulletedText(presentationId, boxId);
        assertEquals(1, response.getReplies().size());
    }

    @Test
    public void testCreateSheetsChart() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        BatchUpdatePresentationResponse response =
                this.snippets.createSheetsChart(
                        presentationId, pageId, DATA_SPREADSHEET_ID, CHART_ID);
        assertEquals(1, response.getReplies().size());
        String chartId = response.getReplies().get(0).getCreateSheetsChart().getObjectId();
        assertNotNull(chartId);
    }

    @Test
    public void testRefreshSheetsChart() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        String chartId =
                this.createTestSheetsChart(presentationId, pageId, DATA_SPREADSHEET_ID, CHART_ID);
        BatchUpdatePresentationResponse response =
                this.snippets.refreshSheetsChart(presentationId, chartId);
        assertEquals(1, response.getReplies().size());
    }
}
