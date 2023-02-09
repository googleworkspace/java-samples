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

import java.security.GeneralSecurityException;
import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Unit test class for Batch Add Students classroom snippet
public class TestBatchAddStudents extends BaseTest {

  @Test
  public void testBatchAddStudents() throws GeneralSecurityException, IOException {
    setup(BatchAddStudents.SCOPES);
    List<String> studentEmails = Arrays.asList("insert_student_1_email", "insert_student_2_email");
    BatchAddStudents.batchAddStudents(testCourse.getId(), studentEmails);
  }
}
