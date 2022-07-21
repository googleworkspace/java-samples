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
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertTrue;

public class TestUploadToFolder extends BaseTest {
    @Test
    public void uploadToFolder() throws IOException, GeneralSecurityException {
        String folderId = CreateFolder.createFolder();
        File file = UploadToFolder.uploadToFolder(folderId);
        assertTrue(file.getParents().get(0).getId().equals(folderId));
        deleteFileOnCleanup(file.getId());
        deleteFileOnCleanup(folderId);
    }
}
