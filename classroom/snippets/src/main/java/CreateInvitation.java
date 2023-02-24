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

// [START classroom_create_invitation]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Invitation;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/* Class to demonstrate the use of Classroom Create Invitation API. */
public class CreateInvitation {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
    tokens/ folder. */
  static ArrayList<String> SCOPES = new ArrayList<>(
      Arrays.asList(ClassroomScopes.CLASSROOM_ROSTERS));

  /**
   * Create an invitation to allow a user to join a course.
   *
   * @param courseId - the course to invite the user to.
   * @param userId - the user to be invited to the course.
   * @return the created invitation.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static Invitation createInvitation(String courseId, String userId)
      throws GeneralSecurityException, IOException {

    // Create the classroom API client.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Classroom service =
        new Classroom.Builder(
            HTTP_TRANSPORT,
            GsonFactory.getDefaultInstance(),
            ClassroomCredentials.getCredentials(HTTP_TRANSPORT, SCOPES))
            .setApplicationName("Classroom samples")
            .build();

    // [START classroom_create_invitation_code_snippet]

    Invitation invitation = null;
    try {
      /* Set the role the user is invited to have in the course. Possible values of CourseRole can be
      found here: https://developers.google.com/classroom/reference/rest/v1/invitations#courserole.*/
      Invitation content = new Invitation()
          .setCourseId(courseId)
          .setUserId(userId)
          .setRole("TEACHER");

      invitation = service.invitations().create(content).execute();

      System.out.printf("User (%s) has been invited to course (%s).\n", invitation.getUserId(),
          invitation.getCourseId());
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The course or user does not exist.\n");
      }
      throw e;
    } catch (Exception e) {
      throw e;
    }
    return invitation;

    // [END classroom_create_invitation_code_snippet]
  }

}
// [END classroom_create_invitation]