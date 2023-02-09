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

import com.google.api.services.classroom.model.Student;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

// Unit test class for Add Student classroom snippet
public class TestAddStudent extends BaseTest {

  private String studentId = "insert_student_id";

  @Test
  public void testAddStudent() throws GeneralSecurityException, IOException {
    // Include the scopes required to run the code example for testing purposes.
    setup(AddStudent.SCOPES);
    Student student =
        AddStudent.addStudent(testCourse.getId(), testCourse.getEnrollmentCode(), this.studentId);
    Assert.assertNotNull("Student not returned.", student);
    Assert.assertNotNull("Course not returned.", student.getCourseId());
    Assert.assertEquals(
        "Student added to wrong course.", testCourse.getId(), student.getCourseId());
  }
}
