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

import com.google.api.services.classroom.model.Guardian;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

// Unit test class for List Guardians classroom snippet
public class TestListGuardians {

  @Test
  public void testListGuardians() throws Exception {
    String studentId = "insert_student_id";
    List<Guardian> guardianList = ListGuardians.listGuardians(studentId);

    Assert.assertTrue("No guardians returned.", guardianList.size() > 0);
  }

}
