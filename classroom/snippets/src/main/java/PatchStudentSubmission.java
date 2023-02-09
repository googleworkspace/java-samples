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

// [START classroom_patch_student_submissions_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.StudentSubmission;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/* Class to demonstrate the use of Classroom Patch StudentSubmissions API. */
public class PatchStudentSubmission {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES =
      new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));
  /**
   * Updates the draft grade and/or assigned grade of a student submission.
   *
   * @param courseId - identifier of the course.
   * @param courseWorkId - identifier of the course work.
   * @param id - identifier of the student submission.
   * @return - the updated student submission.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static StudentSubmission patchStudentSubmission(
      String courseId, String courseWorkId, String id)
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

    // [START classroom_patch_student_submissions_code_snippet]

    StudentSubmission studentSubmission = null;
    try {
      // Updating the draftGrade and assignedGrade fields for the specific student submission.
      StudentSubmission content =
          service
              .courses()
              .courseWork()
              .studentSubmissions()
              .get(courseId, courseWorkId, id)
              .execute();
      content.setAssignedGrade(90.00);
      content.setDraftGrade(80.00);

      // The updated studentSubmission object is returned with the new draftGrade and assignedGrade.
      studentSubmission =
          service
              .courses()
              .courseWork()
              .studentSubmissions()
              .patch(courseId, courseWorkId, id, content)
              .set("updateMask", "draftGrade,assignedGrade")
              .execute();

      /* Prints the updated student submission. */
      System.out.printf(
          "Updated student submission draft grade (%s) and assigned grade (%s).\n",
          studentSubmission.getDraftGrade(), studentSubmission.getAssignedGrade());
    } catch (GoogleJsonResponseException e) {
      // TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf(
            "The courseId (%s), courseWorkId (%s), or studentSubmissionId (%s) does "
                + "not exist.\n",
            courseId, courseWorkId, id);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return studentSubmission;

    // [END classroom_patch_student_submissions_code_snippet]

  }
}
// [END classroom_patch_student_submissions_class]
