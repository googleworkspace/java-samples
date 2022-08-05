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

import com.google.api.services.drive.model.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class DriveSnippetsTest extends BaseTest {

  private DriveSnippets snippets;

  @Before
  public void createSnippets() {
    this.snippets = new DriveSnippets(this.service);
  }

  @Test
  public void createDrive() throws IOException, GeneralSecurityException {
    String id = this.snippets.createDrive();
    assertNotNull(id);
    this.service.drives().delete(id);
  }

  @Test
  public void recoverDrives() throws IOException {
    String id = this.createOrphanedDrive();
    List<Drive> results = this.snippets.recoverDrives(
        "sbazyl@test.appsdevtesting.com");
    assertNotEquals(0, results.size());
    this.service.drives().delete(id).execute();
  }

  private String createOrphanedDrive() throws IOException {
    String driveId = this.snippets.createDrive();
    PermissionList response = this.service.permissions().list(driveId)
        .setSupportsAllDrives(true)
        .execute();
    for (Permission permission : response.getPermissions()) {
      this.service.permissions().delete(driveId, permission.getId())
          .setSupportsAllDrives(true)
          .execute();
    }
    return driveId;
  }
}
