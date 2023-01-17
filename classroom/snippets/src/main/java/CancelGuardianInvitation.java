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

// [START classroom_cancel_guardian_invitation_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.GuardianInvitation;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Classroom Patch Guardian Invitation API. */
public class CancelGuardianInvitation {
  /**
   * Cancel a guardian invitation by modifying the state of the invite.
   *
   * @param studentId - the id of the student.
   * @param invitationId - the id of the guardian invitation to modify.
   * @return - the modified guardian invitation.
   * @throws IOException - if credentials file not found.
   */
  public static GuardianInvitation cancelGuardianInvitation(String studentId, String invitationId)
      throws Exception {

    /* Scopes required by this API call. If modifying these scopes, delete your previously saved
    tokens/ folder. */
    final List<String> SCOPES =
        Collections.singletonList(ClassroomScopes.CLASSROOM_GUARDIANLINKS_STUDENTS);

    // Create the classroom API client
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Classroom service = new Classroom.Builder(HTTP_TRANSPORT,
        GsonFactory.getDefaultInstance(),
        ClassroomCredentials.getCredentials(HTTP_TRANSPORT, SCOPES))
        .setApplicationName("Classroom samples")
        .build();

    // [START classroom_cancel_guardian_invitation_code_snippet]

    GuardianInvitation guardianInvitation = null;

    try {
      /* Change the state of the GuardianInvitation from PENDING to COMPLETE. See
    https://developers.google.com/classroom/reference/rest/v1/userProfiles.guardianInvitations#guardianinvitationstate
    for other possible states of guardian invitations. */
      GuardianInvitation content = service.userProfiles().guardianInvitations()
          .get(studentId, invitationId)
          .execute();
      content.setState("COMPLETE");

      guardianInvitation = service.userProfiles().guardianInvitations()
          .patch(studentId, invitationId, content)
          .set("updateMask", "state")
          .execute();

      System.out.printf("Invitation (%s) state set to %s\n.", guardianInvitation.getInvitationId(),
          guardianInvitation.getState());
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("There is no record of studentId (%s) or invitationId (%s).", studentId,
            invitationId);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return guardianInvitation;

    // [END classroom_cancel_guardian_invitation_code_snippet]
  }
}
// [END classroom_cancel_guardian_invitation_class]