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

// Unit testcase for createSlide snippet
public class TestCreateSlide extends BaseTest {

  @Test
  public void testCreateSlide() throws IOException {
    String presentationId = createTestPresentation();
    BatchUpdatePresentationResponse response =
        CreateSlide.createSlide(presentationId);
    assertNotNull(response);
    assertEquals(1, response.getReplies().size());
    String pageId = response.getReplies().get(0).getCreateSlide().getObjectId();
    assertNotNull(pageId);
    deleteFileOnCleanup(presentationId);
  }
}
