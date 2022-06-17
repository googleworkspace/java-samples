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

import com.google.api.services.drive.model.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

// Unit test class for recover-drives Drive snippet
public class TestRecoverDrives extends BaseTest{
    @Test
    public void recoverDrives() throws IOException {
        String id = this.createOrphanedDrive();
        List<Drive> results = RecoverDrive.recoverDrives(
                "sbazyl@test.appsdevtesting.com");
        assertNotEquals(0, results.size());
        this.service.drives().delete(id).execute();
    }

    private String createOrphanedDrive() throws IOException {
        String driveId = CreateDrive.createDrive();
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
