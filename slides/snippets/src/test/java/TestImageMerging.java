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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// Unit test class for imageMerging snippet
public class TestImageMerging extends BaseTest{

    private final String IMAGE_URL =
            "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";
    private final String TEMPLATE_PRESENTATION_ID = "1wJUN1B5CQ2wQOBzmz2apky48QNK1OsE2oNKHPMLpKDc";
    private final String CUSTOMER_NAME = "Fake Customer";
    @Test
    public void testImageMerge() throws IOException {
        BatchUpdatePresentationResponse response =
                ImageMerging.imageMerging(TEMPLATE_PRESENTATION_ID, IMAGE_URL, CUSTOMER_NAME);
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
}
