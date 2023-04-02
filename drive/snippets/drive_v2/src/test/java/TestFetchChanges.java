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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.junit.Test;

public class TestFetchChanges extends BaseTest {
  @Test
  public void fetchChanges() throws IOException {
    String startPageToken = FetchStartPageToken.fetchStartPageToken();
    this.createTestBlob();
    String newStartPageToken = FetchChanges.fetchChanges(startPageToken);
    assertNotNull(newStartPageToken);
    assertNotEquals(startPageToken, newStartPageToken);
  }
}
