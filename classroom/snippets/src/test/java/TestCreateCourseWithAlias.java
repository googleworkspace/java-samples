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

import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseAlias;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

// Unit test class for Create Alias classroom snippet
public class TestCreateCourseWithAlias extends BaseTest {

  @Test
  public void testCreateCourseWithAlias() throws GeneralSecurityException, IOException {
    setup(CreateCourseWithAlias.SCOPES);
    Course course = CreateCourseWithAlias.createCourseWithAlias();
    List<CourseAlias> courseAliases =
        service.courses().aliases().list(course.getId()).execute().getAliases();
    Assert.assertNotNull("Course not returned.", course);
    Assert.assertTrue("No course aliases exist.", courseAliases.size() > 0);
    deleteCourse(course.getId());
  }
}
