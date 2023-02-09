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

import com.google.api.services.classroom.model.Teacher;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;

// Unit test class for Add Teacher classroom snippet
public class TestAddTeacher extends BaseTest {

  private String teacherEmail = "insert_teacher_email";

  @Test
  public void testAddTeacher() throws GeneralSecurityException, IOException {
    // Include the scopes required to run the code example for testing purposes.
    setup(AddTeacher.SCOPES);
    Teacher teacher = AddTeacher.addTeacher(testCourse.getId(), this.teacherEmail);
    Assert.assertNotNull("Teacher not returned.", teacher);
    Assert.assertEquals(
        "Teacher added to wrong course.", testCourse.getId(), teacher.getCourseId());
  }
}
