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

// [START classroom_add_teacher]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Teacher;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/* Class to demonstrate the use of Classroom Add Teacher API */
public class AddTeacher {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES =
      new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_ROSTERS));

  /**
   * Add teacher to a specific course.
   *
   * @param courseId - Id of the course.
   * @param teacherEmail - Email address of the teacher.
   * @return newly created teacher
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static Teacher addTeacher(String courseId, String teacherEmail)
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

    Teacher teacher = new Teacher().setUserId(teacherEmail);
    try {
      // Add a teacher to a specified course
      teacher = service.courses().teachers().create(courseId, teacher).execute();
      // Prints the course id with the teacher name
      System.out.printf(
          "User '%s' was added as a teacher to the course with ID '%s'.\n",
          teacher.getProfile().getName().getFullName(), courseId);
    } catch (GoogleJsonResponseException e) {
      // TODO(developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 409) {
        System.out.printf("User '%s' is already a member of this course.\n", teacherEmail);
      } else if (error.getCode() == 403) {
        System.out.println("The caller does not have permission.\n");
      } else {
        throw e;
      }
    }
    return teacher;
  }
}
// [END classroom_add_teacher]
