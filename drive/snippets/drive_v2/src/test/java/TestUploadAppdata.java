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

import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

// Unit test class for testing of UploadAppData snippet
public class TestUploadAppdata extends BaseTest{
    @Test
    public void uploadAppData()
            throws IOException, GeneralSecurityException {
        String id = UploadAppData.uploadAppData();
        assertNotNull(id);
        deleteFileOnCleanup(id);
    }
}
