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

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.*;

public class AppDataSnippetsTest extends BaseTest {

  private AppDataSnippets snippets;

  @Before
  public void createSnippets() {
    this.snippets = new AppDataSnippets(this.service);
  }

  @Test
  public void fetchAppDataFolder() throws IOException, GeneralSecurityException {
    String id = this.snippets.fetchAppDataFolder();
    assertNotNull(id);
  }

  @Test
  public void uploadAppData()
      throws IOException, GeneralSecurityException {
    String id = this.snippets.uploadAppData();
    assertNotNull(id);
    deleteFileOnCleanup(id);
  }

  @Test
  public void listAppData() throws IOException, GeneralSecurityException {
    String id = this.snippets.uploadAppData();
    deleteFileOnCleanup(id);
    FileList files = this.snippets.listAppData();
    assertNotEquals(0, files.getFiles().size());
  }

}
