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

// [START classroom_cancel_guardian_invitation_class]

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.GuardianInvitation;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

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
      throws IOException {
    /* Load pre-authorized user credentials from the environment.
     TODO(developer) - See https://developers.google.com/identity for
      guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_GUARDIANLINKS_STUDENTS));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client.
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
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

      System.out.printf("Invitation (%s) state set to %s", guardianInvitation.getInvitationId(),
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