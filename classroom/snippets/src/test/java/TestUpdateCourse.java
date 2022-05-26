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
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

// Unit test class for Update Course classroom snippet
public class TestUpdateCourse extends BaseTest{

    @Test
    public void testUpdateCourse() throws IOException {
        Course course = UpdateCourse.updateCourse(testCourse.getId());
        Assert.assertNotNull("Course not returned.", course);
        Assert.assertEquals("Wrong course returned.", testCourse.getId(), course.getId());
    }
}
