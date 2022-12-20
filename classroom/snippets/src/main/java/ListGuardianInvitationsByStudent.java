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


// [START classroom_list_guardian_invitations_class]

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.GuardianInvitation;
import com.google.api.services.classroom.model.ListGuardianInvitationsResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Classroom List Guardian Invitations API. */
public class ListGuardianInvitationsByStudent {
  /**
   * Retrieves guardian invitations by student.
   *
   * @param studentId - the id of the student.
   * @return a list of guardian invitations that were sent for a specific student.
   * @throws IOException - if credentials file not found.
   */
  public static List<GuardianInvitation> listGuardianInvitationsByStudent(String studentId)
      throws IOException {
    /* Load pre-authorized user credentials from the environment.
     TODO(developer) - See https://developers.google.com/identity for
      guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_GUARDIANLINKS_STUDENTS_READONLY));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client.
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Classroom samples")
        .build();

    // [START classroom_list_guardian_invitations_code_snippet]

    List<GuardianInvitation> guardianInvitations = new ArrayList<>();
    String pageToken = null;

    try {
      do {
        ListGuardianInvitationsResponse response = service.userProfiles().guardianInvitations()
            .list(studentId)
            .execute();

        if (response.getGuardianInvitations() != null) {
          guardianInvitations.addAll(response.getGuardianInvitations());
          pageToken = response.getNextPageToken();
        }
      } while (pageToken != null);

      if (guardianInvitations.isEmpty()) {
        System.out.println("No guardian invitations found.");
      } else {
        for (GuardianInvitation invitation : guardianInvitations) {
          System.out.printf("Guardian invitation: %s\n", invitation.getInvitedEmailAddress());
        }
      }
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("There is no record of studentId (%s).", studentId);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return guardianInvitations;

    // [END classroom_list_guardian_invitations_code_snippet]
  }
}
// [END classroom_list_guardian_invitations_class]
