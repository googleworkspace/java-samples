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

// Unit testcase for createImage snippet
public class TestCreateImage extends BaseTest {

    private final String IMAGE_URL =
            "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png";

    @Test
    public void testCreateImage() throws IOException {
        String presentationId = createTestPresentation();
        String slideId = createTestSlide(presentationId);
        BatchUpdatePresentationResponse response = CreateImage.createImage(
                presentationId, slideId, IMAGE_URL);
        assertEquals(1, response.getReplies().size());
        String imageId = response.getReplies().get(0).getCreateImage().getObjectId();
        assertNotNull(imageId);
    }
}
