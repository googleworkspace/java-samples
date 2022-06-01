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

import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

// Unit testcase for spreadsheet batch update values snippet
public class TestBatchUpdateValues extends BaseTest {

    @Test
    public void testBatchUpdateValues() throws IOException {
        String spreadsheetId = Create.createSpreadsheet("Test Spreadsheet");
        List<List<Object>> values = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D"));
        BatchUpdateValuesResponse result =
                BatchUpdateValues.batchUpdateValues(spreadsheetId, "A1:B2", "USER_ENTERED",
                        values);
        assertEquals(1, result.getResponses().size());
        assertEquals(2, result.getTotalUpdatedRows().intValue());
        assertEquals(2, result.getTotalUpdatedColumns().intValue());
        assertEquals(4, result.getTotalUpdatedCells().intValue());
        deleteFileOnCleanup(spreadsheetId);
    }
}
