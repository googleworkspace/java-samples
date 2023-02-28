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

import com.google.api.services.classroom.model.CourseAlias;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

// Unit test class for Add Alias classroom snippet
public class TestAddAliasToCourse extends BaseTest {

  @Test
  public void testAddCourseAlias() throws GeneralSecurityException, IOException {
    // Include the scopes required to run the code example for testing purposes.
    setup(AddAliasToCourse.SCOPES);
    CourseAlias courseAlias = AddAliasToCourse.addAliasToCourse(testCourse.getId());
    List<CourseAlias> courseAliases =
        service.courses().aliases().list(testCourse.getId()).execute().getAliases();
    Assert.assertNotNull("Course alias not returned.", courseAlias);
    Assert.assertTrue("No course aliases exist.", courseAliases.size() > 0);
  }
}
