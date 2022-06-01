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

import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

// Unit testcase for spreadsheet batch get values snippet
public class TestBatchGetValues extends BaseTest{

    @Test
    public void testBatchGetValues() throws IOException {
        String spreadsheetId = Create.createSpreadsheet("Test Spreadsheet");
        populateValuesWithStrings(spreadsheetId);
        List<String> ranges = Arrays.asList("A1:A3", "B1:C1");
        BatchGetValuesResponse result = BatchGetValues.batchGetValues(spreadsheetId,
                ranges);
        List<ValueRange> valueRanges = result.getValueRanges();
        assertEquals(2, valueRanges.size());
        List<List<Object>> values = valueRanges.get(0).getValues();
        assertEquals(3, values.size());
        deleteFileOnCleanup(spreadsheetId);
    }
}
