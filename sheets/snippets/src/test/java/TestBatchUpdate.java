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

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.FindReplaceResponse;
import com.google.api.services.sheets.v4.model.Response;
import org.junit.Test;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;

// Unit testcase for spreadsheet batch update snippet
public class TestBatchUpdate extends BaseTest{

    @Test
    public void testBatchUpdate() throws IOException {
        String spreadsheetId = Create.createSpreadsheet("Test Spreadsheet");
        populateValuesWithStrings(spreadsheetId);
        BatchUpdateSpreadsheetResponse response = BatchUpdate.batchUpdate(spreadsheetId, "New Title", "Hello", "Goodbye");
        List<Response> replies = response.getReplies();
        assertEquals(2, replies.size());
        FindReplaceResponse findReplaceResponse = replies.get(1).getFindReplace();
        assertEquals(100, findReplaceResponse.getOccurrencesChanged().intValue());
        deleteFileOnCleanup(spreadsheetId);
    }
}
