import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SpreadsheetSnippetsTest extends BaseTest {
    private SpreadsheetSnippets snippets;

    @Before
    public void createSnippets() {
        this.snippets = new SpreadsheetSnippets(this.service);
    }

    @Test
    public void testCreate() throws IOException {
        String id = this.snippets.create("Title");
        assertNotNull(id);
        this.deleteFileOnCleanup(id);
    }

    @Test
    public void testBatchUpdate() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        this.populateValuesWithStrings(spreadsheetId);
        BatchUpdateSpreadsheetResponse response =
                this.snippets.batchUpdate(spreadsheetId, "New Title", "Hello", "Goodbye");
        List<Response> replies = response.getReplies();
        assertEquals(2, replies.size());
        FindReplaceResponse findReplaceResponse = replies.get(1).getFindReplace();
        assertEquals(100, findReplaceResponse.getOccurrencesChanged().intValue());
    }

    @Test
    public void testConditionalFormat() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        this.populateValuesWithNumbers(spreadsheetId);
        BatchUpdateSpreadsheetResponse response =
                this.snippets.conditionalFormat(spreadsheetId);
        assertEquals(spreadsheetId, response.getSpreadsheetId());
        assertEquals(2, response.getReplies().size());
    }

    @Test
    public void testGetValues() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        this.populateValuesWithStrings(spreadsheetId);
        ValueRange result = this.snippets.getValues(spreadsheetId, "A1:C2");
        List<List<Object>> values = result.getValues();
        assertEquals(2, values.size());
        assertEquals(3, values.get(0).size());
    }

    @Test
    public void testBatchGetValues() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        this.populateValuesWithStrings(spreadsheetId);
        List<String> ranges = Arrays.asList("A1:A3", "B1:C1");
        BatchGetValuesResponse result = this.snippets.batchGetValues(spreadsheetId, ranges);
        List<ValueRange> valueRanges = result.getValueRanges();
        assertEquals(2, valueRanges.size());
        List<List<Object>> values = valueRanges.get(0).getValues();
        assertEquals(3, values.size());
    }

    @Test
    public void testUpdateValues() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D"));
        UpdateValuesResponse result =
                this.snippets.updateValues(spreadsheetId, "A1:B2", "USER_ENTERED", values);
        assertEquals(2, result.getUpdatedRows().intValue());
        assertEquals(2, result.getUpdatedColumns().intValue());
        assertEquals(4, result.getUpdatedCells().intValue());
    }

    @Test
    public void testBatchUpdateValues() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        List<List<Object>> values = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D"));
        BatchUpdateValuesResponse result =
                this.snippets.batchUpdateValues(spreadsheetId, "A1:B2", "USER_ENTERED", values);
        assertEquals(1, result.getResponses().size());
        assertEquals(2, result.getTotalUpdatedRows().intValue());
        assertEquals(2, result.getTotalUpdatedColumns().intValue());
        assertEquals(4, result.getTotalUpdatedCells().intValue());
    }

    @Test
    public void testAppendValues() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        this.populateValuesWithStrings(spreadsheetId);
        List<List<Object>> values = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D"));
        AppendValuesResponse result =
                this.snippets.appendValues(spreadsheetId, "A1:B2", "USER_ENTERED", values);
        assertEquals("Sheet1!A1:J10", result.getTableRange());
        UpdateValuesResponse updates = result.getUpdates();
        assertEquals(2, updates.getUpdatedRows().intValue());
        assertEquals(2, updates.getUpdatedColumns().intValue());
        assertEquals(4, updates.getUpdatedCells().intValue());
    }

    @Test
    public void testPivotTable() throws IOException {
        String spreadsheetId = this.createTestSpreadsheet();
        BatchUpdateSpreadsheetResponse result = this.snippets.pivotTables(spreadsheetId);
        assertNotNull(result);
    }
}
