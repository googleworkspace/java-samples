// Copyright 2023 Google LLC
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

import com.google.api.services.classroom.model.GuardianInvitation;
import org.junit.Assert;
import org.junit.Test;

// Unit test class for Cancel Guardian Invitation classroom snippet
public class TestCancelGuardianInvitation {

  @Test
  public void testCancelGuardianInvitation() throws Exception {
    String studentId = "insert_student_id";
    String guardianEmail = "insert_guardian_email";

    GuardianInvitation invitation = CreateGuardianInvitation.createGuardianInvitation(studentId,
        guardianEmail);

    GuardianInvitation guardianInvitation = CancelGuardianInvitation.cancelGuardianInvitation(studentId,
        invitation.getInvitationId());

    Assert.assertTrue("Guardian invitation not canceled.", guardianInvitation != null);
    Assert.assertTrue("Guardian invitation state not updated.", guardianInvitation.getState()
        .equals("COMPLETE"));
  }
}
