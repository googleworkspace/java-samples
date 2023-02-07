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

// Unit test class for List Course Aliases classroom snippet
public class TestListCourseAliases extends BaseTest {

  @Test
  public void testListCourseAliases() throws GeneralSecurityException, IOException {
    setup(ListCourseAliases.SCOPES);
    List<CourseAlias> courseAliases = ListCourseAliases.listCourseAliases(testCourse.getId());
    Assert.assertTrue("Incorrect number of course aliases returned.", courseAliases.size() == 1);
  }
}