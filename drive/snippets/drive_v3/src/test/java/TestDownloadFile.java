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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Test;

public class TestDownloadFile extends BaseTest {
  @Test
  public void downloadFile() throws IOException, GeneralSecurityException {
    String id = createTestBlob();
    ByteArrayOutputStream out = DownloadFile.downloadFile(id);
    byte[] bytes = out.toByteArray();
    assertEquals((byte) 0xFF, bytes[0]);
    assertEquals((byte) 0xD8, bytes[1]);
  }
}
