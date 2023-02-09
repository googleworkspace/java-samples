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

// [START classroom_batch_add_students]

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Student;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate the use of Classroom Batch Add Students API */
public class BatchAddStudents {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES =
      new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_ROSTERS));

  /**
   * Add multiple students in a specified course.
   *
   * @param courseId - Id of the course to add students.
   * @param studentEmails - Email address of the students.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static void batchAddStudents(String courseId, List<String> studentEmails)
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

    BatchRequest batch = service.batch();
    JsonBatchCallback<Student> callback =
        new JsonBatchCallback<>() {
          public void onSuccess(Student student, HttpHeaders responseHeaders) {
            System.out.printf(
                "User '%s' was added as a student to the course.\n",
                student.getProfile().getName().getFullName());
          }

          public void onFailure(GoogleJsonError error, HttpHeaders responseHeaders) {
            System.out.printf("Error adding student to the course: %s\n", error.getMessage());
          }
        };
    for (String studentEmail : studentEmails) {
      Student student = new Student().setUserId(studentEmail);
      service.courses().students().create(courseId, student).queue(batch, callback);
    }
    batch.execute();
  }
}
// [END classroom_batch_add_students]
