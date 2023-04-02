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

import static org.junit.Assert.assertEquals;

import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

// Unit testcase for spreadsheet get values snippet
public class TestGetValues extends BaseTest {

  @Test
  public void testGetValues() throws IOException {
    String spreadsheetId = Create.createSpreadsheet("Test Spreadsheet");
    populateValuesWithStrings(spreadsheetId);
    ValueRange result = GetValues.getValues(spreadsheetId, "A1:C2");
    List<List<Object>> values = result.getValues();
    assertEquals(2, values.size());
    assertEquals(3, values.get(0).size());
    deleteFileOnCleanup(spreadsheetId);
  }
}
