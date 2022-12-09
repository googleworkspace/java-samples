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

import com.google.api.services.classroom.model.Topic;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

// Unit test class for Get Topic classroom snippet
public class TestGetTopic extends BaseTest {

  @Test
  public void testGetTopic() throws IOException {
    Topic topic = CreateTopic.createTopic(testCourse.getId());
    Topic getTopic = GetTopic.getTopic(testCourse.getId(), topic.getTopicId());
    Assert.assertNotNull("Topic could not be created.", topic);
    Assert.assertNotNull("Topic could not be retrieved.", getTopic);
    Assert.assertEquals("Wrong topic was retrieved.", topic, getTopic);
  }
}
