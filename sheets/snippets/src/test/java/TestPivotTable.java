import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.assertNotNull;

// Unit testcase for spreadsheet pivot table snippet
public class TestPivotTable extends BaseTest{

    @Test
    public void testPivotTable() throws IOException {
        String spreadsheetId = Create.createSpreadsheet("Test Spreadsheet");
        BatchUpdateSpreadsheetResponse result = PivotTables.pivotTables(spreadsheetId);
        assertNotNull(result);
        deleteFileOnCleanup(spreadsheetId);
    }
}
