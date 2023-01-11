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


// [START classroom_create_guardian_invitation_class]

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

/* Class to demonstrate the use of Classroom Create Guardian Invitation API. */
public class CreateGuardianInvitation {
  /**
   * Creates a guardian invitation by sending an email to the guardian for confirmation.
   *
   * @param studentId - the id of the student.
   * @param guardianEmail - email to send the guardian invitation to.
   * @return - the newly created guardian invitation.
   * @throws IOException - if credentials file not found.
   */
  public static GuardianInvitation createGuardianInvitation(String studentId, String guardianEmail)
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

    // [START classroom_create_guardian_invitation_code_snippet]

    GuardianInvitation guardianInvitation = null;

    /* Create a GuardianInvitation object with state set to PENDING. See
    https://developers.google.com/classroom/reference/rest/v1/userProfiles.guardianInvitations#guardianinvitationstate
    for other possible states of guardian invitations. */
    GuardianInvitation content = new GuardianInvitation()
        .setStudentId(studentId)
        .setInvitedEmailAddress(guardianEmail)
        .setState("PENDING");
    try {
      guardianInvitation = service.userProfiles().guardianInvitations()
          .create(studentId, content)
          .execute();

      System.out.printf("Invitation created: %s\n", guardianInvitation.getInvitationId());
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("There is no record of studentId: %s", studentId);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return guardianInvitation;

    // [END classroom_create_guardian_invitation_code_snippet]
  }
}
// [END classroom_create_guardian_invitation_class]