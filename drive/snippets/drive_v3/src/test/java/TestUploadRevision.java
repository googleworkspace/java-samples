/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Test;

public class TestUploadRevision extends BaseTest {
  @Test
  public void uploadRevision() throws IOException, GeneralSecurityException {
    String id = UploadBasic.uploadBasic();
    assertNotNull(id);
    deleteFileOnCleanup(id);
    String id2 = UploadRevision.uploadRevision(id);
    assertEquals(id, id2);
  }
}
