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


// [START classroom_get_guardian_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Guardian;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Classroom Get Guardian API. */
public class GetGuardian {
  /**
   * Retrieve a guardian for a specific student.
   *
   * @param studentId - the id of the student the guardian belongs to.
   * @param guardianId - the id of the guardian to delete.
   * @throws IOException - if credentials file not found.
   */
  public static void getGuardian(String studentId, String guardianId) throws Exception {
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

    // [START classroom_get_guardian_code_snippet]
    Guardian guardian = null;

    try {
      guardian = service.userProfiles().guardians().get(studentId, guardianId)
          .execute();
      System.out.printf("Guardian retrieved: %s", guardian.getInvitedEmailAddress());
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.err.printf("There is no record of guardianId (%s).\n", guardianId);
        throw e;
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    // [END classroom_get_guardian_code_snippet]
  }
}
// [END classroom_get_guardian_class]