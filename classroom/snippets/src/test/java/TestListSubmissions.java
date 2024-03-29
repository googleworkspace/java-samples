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

import com.google.api.services.classroom.model.StudentSubmission;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

// Unit test class for ListSubmissions classroom snippet
public class TestListSubmissions extends BaseTest {

  @Test
  public void testListSubmissions() throws GeneralSecurityException, IOException {
    setup(ListSubmissions.SCOPES);
    List<StudentSubmission> submissions = ListSubmissions.listSubmissions(testCourse.getId(), "-");
    Assert.assertNotNull("No submissions returned.", submissions);
  }
}
