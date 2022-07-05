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

import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// Unit test class for createSheetsChart snippet
public class TestCreateSheetsChart extends BaseTest{
    private final String DATA_SPREADSHEET_ID = "14KaZMq2aCAGt5acV77zaA_Ps8aDt04G7T0ei4KiXLX8";
    private final Integer CHART_ID = 1107320627;
    @Test
    public void testCreateSheetsChart() throws IOException {
        String presentationId = this.createTestPresentation();
        String pageId = this.createTestSlide(presentationId);
        BatchUpdatePresentationResponse response =
                CreateSheetsChart.createSheetsChart(
                        presentationId, pageId, DATA_SPREADSHEET_ID, CHART_ID);
        assertEquals(1, response.getReplies().size());
        String chartId = response.getReplies().get(0).getCreateSheetsChart().getObjectId();
        assertNotNull(chartId);
    }
}
