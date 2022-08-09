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

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.StartPageToken;
import java.io.IOException;

public class ChangeSnippets {

  private Drive service;

  public ChangeSnippets(Drive service) {
    this.service = service;
  }

  public String fetchStartPageToken() throws IOException {
    Drive driveService = this.service;
    // [START fetchStartPageToken]
    StartPageToken response = driveService.changes()
        .getStartPageToken().execute();
    System.out.println("Start token: " + response.getStartPageToken());
    // [END fetchStartPageToken]
    return response.getStartPageToken();
  }

  public String fetchChanges(String savedStartPageToken) throws IOException {
    Drive driveService = this.service;
    // [START fetchChanges]
    // Begin with our last saved start token for this user or the
    // current token from getStartPageToken()
    String pageToken = savedStartPageToken;
    while (pageToken != null) {
      ChangeList changes = driveService.changes().list(pageToken)
          .execute();
      for (Change change : changes.getChanges()) {
        // Process change
        System.out.println("Change found for file: " + change.getFileId());
      }
      if (changes.getNewStartPageToken() != null) {
        // Last page, save this token for the next polling interval
        savedStartPageToken = changes.getNewStartPageToken();
      }
      pageToken = changes.getNextPageToken();
    }
    // [END fetchChanges]
    return savedStartPageToken;
  }
}
