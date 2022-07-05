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
import com.google.api.services.slides.v1.model.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// Unit test class for textMerging snippet
public class TestTextMerging extends BaseTest{
    private final String TEMPLATE_PRESENTATION_ID = "1wJUN1B5CQ2wQOBzmz2apky48QNK1OsE2oNKHPMLpKDc";
    private final String DATA_SPREADSHEET_ID = "14KaZMq2aCAGt5acV77zaA_Ps8aDt04G7T0ei4KiXLX8";
    @Test
    public void testTextMerge() throws IOException {
        List<BatchUpdatePresentationResponse> responses =
                TextMerging.textMerging(TEMPLATE_PRESENTATION_ID, DATA_SPREADSHEET_ID);
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
}
