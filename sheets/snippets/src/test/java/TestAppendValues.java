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

import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

// Unit testcase for spreadsheet append values snippet
public class TestAppendValues extends BaseTest{

    @Test
    public void testAppendValues() throws IOException {
        String spreadsheetId = Create.createSpreadsheet("Test Spreadsheet");
        populateValuesWithStrings(spreadsheetId);
        List<List<Object>> values = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D"));
        AppendValuesResponse result = AppendValues.appendValues(spreadsheetId, "A1:B2", "USER_ENTERED",
                        values);
        assertEquals("Sheet1!A1:J10", result.getTableRange());
        UpdateValuesResponse updates = result.getUpdates();
        assertEquals(2, updates.getUpdatedRows().intValue());
        assertEquals(2, updates.getUpdatedColumns().intValue());
        assertEquals(4, updates.getUpdatedCells().intValue());
        deleteFileOnCleanup(spreadsheetId);
    }
}
