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


// [START classroom_list_aliases_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.CourseAlias;
import com.google.api.services.classroom.model.ListCourseAliasesResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate the use of Classroom List Alias API */
public class ListCourseAliases {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES = new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_COURSES));

  /**
   * Retrieve the aliases for a course.
   *
   * @param courseId - id of the course.
   * @return list of course aliases
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static List<CourseAlias> listCourseAliases(String courseId)
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

    // [START classroom_list_aliases_code_snippet]

    String pageToken = null;
    List<CourseAlias> courseAliases = new ArrayList<>();

    try {
      // List of aliases of specified course
      do {
        ListCourseAliasesResponse response = service.courses().aliases().list(courseId)
            .setPageSize(100)
            .setPageToken(pageToken)
            .execute();
        courseAliases.addAll(response.getAliases());
        pageToken = response.getNextPageToken();
      } while (pageToken != null);

      if (courseAliases.isEmpty()) {
        System.out.println("No aliases found.");
      } else {
        System.out.println("Aliases:");
        for (CourseAlias courseAlias : courseAliases) {
          System.out.println(courseAlias.getAlias());
        }
      }
    } catch (GoogleJsonResponseException e) {
      // TODO(developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.err.println("Course does not exist.\n");
      } else {
        throw e;
      }
    }
    return courseAliases;

    // [END classroom_list_aliases_code_snippet]
  }
}
// [END classroom_list_aliases_class]