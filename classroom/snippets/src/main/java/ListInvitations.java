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

// [START classroom_list_invitation]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Invitation;
import com.google.api.services.classroom.model.ListInvitationsResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate the use of Classroom List Invitation API. */
public class ListInvitations {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES = new ArrayList<>(
      Arrays.asList(ClassroomScopes.CLASSROOM_ROSTERS));

  /**
   * Returns a list of invitations that the requesting user is permitted to view,
   * restricted to those that match the specified courseId and/or userId.
   *
   * @param courseId - a specified course.
   * @param userId -  a specified userId.
   * @return list of invitations for the specified courseId and/or userId.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static List<Invitation> listInvitations(String courseId, String userId)
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

    // [START classroom_list_invitation_code_snippet]
    List<Invitation> invitations = new ArrayList<>();
    String pageToken = null;
    try {
      do {
        ListInvitationsResponse response = service.invitations().list()
            .setCourseId(courseId)
            .setUserId(userId)
            .setPageToken(pageToken)
            .execute();

        /* Ensure that the response is not null before retrieving data from it to avoid errors. */
        if (response.getInvitations() != null) {
          invitations.addAll(response.getInvitations());
          pageToken = response.getNextPageToken();
        }
      } while (pageToken != null);

      if (invitations.isEmpty()) {
        System.out.println("No invitations found.");
      } else {
        for (Invitation invitation : invitations) {
          System.out.printf("Invitation id (%s).\n", invitation.getId());
        }
      }
    } catch (GoogleJsonResponseException e) {
      // TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The courseId or userId does not exist.\n");
      }
      throw e;
    } catch (Exception e) {
      throw e;
    }
    return invitations;
    // [END classroom_list_invitation_code_snippet]
  }
}
// [END classroom_list_invitation]