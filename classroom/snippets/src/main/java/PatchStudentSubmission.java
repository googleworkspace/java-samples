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

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.StudentSubmission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Classroom Patch StudentSubmissions API. */
public class PatchStudentSubmission {
  /**
   * Updates the draft grade and/or assigned grade of a student submission.
   *
   * @param courseId - identifier of the course.
   * @param courseWorkId - identifier of the course work.
   * @param id - identifier of the student submission.
   * @return - updated StudentSubmission instance.
   * @throws IOException - if credentials file not found.
   */
  public static StudentSubmission patchStudentSubmission(String courseId, String courseWorkId,
      String id) throws IOException {
    /* Load pre-authorized user credentials from the environment.
     TODO(developer) - See https://developers.google.com/identity for
      guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client.
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Classroom samples")
        .build();

    // [START classroom_patch_student_submissions_code_snippet]

    StudentSubmission studentSubmission = null;
    try {
      // Updating the draftGrade and assignedGrade fields for the specific student submission.
      StudentSubmission content = service.courses().courseWork().studentSubmissions()
          .get(courseId, courseWorkId, id)
          .execute();
      content.setAssignedGrade(80.00);
      content.setDraftGrade(90.00);

      // The updated studentSubmission object is returned with the new draftGrade and assignedGrade.
      studentSubmission = service.courses().courseWork().studentSubmissions()
          .patch(courseId, courseWorkId, id, content)
          .set("updateMask", "draftGrade,assignedGrade")
          .execute();
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The courseId (%s), courseWorkId (%s), or studentSubmissionId (%s) does "
            + "not exist.\n", courseId, courseWorkId, id);
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