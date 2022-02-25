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
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Teacher;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Classroom Add Teacher API */
public class AddTeacher {
    /**
     * Add teacher to a specific course.
     *
     * @param courseId - Id of the course.
     * @param teacherEmail - Email address of the teacher.
     * @return newly created teacher
     * @throws IOException - if credentials file not found.
     */
    public static Teacher addTeacher(String courseId, String teacherEmail)
            throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_ROSTERS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the classroom API client
        Classroom service = new Classroom.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Classroom samples")
                .build();

        Teacher teacher = new Teacher().setUserId(teacherEmail);
        try {
            // Add a teacher to a specified course
            teacher = service.courses().teachers().create(courseId, teacher).execute();
            // Prints the course id with the teacher name
            System.out.printf("User '%s' was added as a teacher to the course with ID '%s'.\n",
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