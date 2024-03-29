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

// [START classroom_create_coursework_class]
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.Link;
import com.google.api.services.classroom.model.Material;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate the use of Classroom Create CourseWork API. */
public class CreateCourseWork {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES =
      new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));

  /**
   * Creates course work.
   *
   * @param courseId - id of the course to create coursework in.
   * @return - newly created CourseWork object.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static CourseWork createCourseWork(String courseId)
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

    // [START classroom_create_coursework_code_snippet]

    CourseWork courseWork = null;
    try {
      // Create a link to add as a material on course work.
      Link articleLink =
          new Link()
              .setTitle("SR-71 Blackbird")
              .setUrl("https://www.lockheedmartin.com/en-us/news/features/history/blackbird.html");

      // Create a list of Materials to add to course work.
      List<Material> materials = Arrays.asList(new Material().setLink(articleLink));

      /* Create new CourseWork object with the material attached.
      Set workType to `ASSIGNMENT`. Possible values of workType can be found here:
      https://developers.google.com/classroom/reference/rest/v1/CourseWorkType
      Set state to `PUBLISHED`. Possible values of state can be found here:
      https://developers.google.com/classroom/reference/rest/v1/courses.courseWork#courseworkstate */
      CourseWork content =
          new CourseWork()
              .setTitle("Supersonic aviation")
              .setDescription(
                  "Read about how the SR-71 Blackbird, the world’s fastest and "
                      + "highest-flying manned aircraft, was built.")
              .setMaterials(materials)
              .setWorkType("ASSIGNMENT")
              .setState("PUBLISHED");

      courseWork = service.courses().courseWork().create(courseId, content).execute();

      /* Prints the created courseWork. */
      System.out.printf("CourseWork created: %s\n", courseWork.getTitle());
    } catch (GoogleJsonResponseException e) {
      // TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The courseId does not exist: %s.\n", courseId);
      } else {
        throw e;
      }
      throw e;
    } catch (Exception e) {
      throw e;
    }
    return courseWork;

    // [END classroom_create_coursework_code_snippet]
  }
}
// [END classroom_create_coursework_class]
