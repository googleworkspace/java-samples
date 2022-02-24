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


// [START classroom_add_student]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Student;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Classroom Add Student API */
public class AddStudent {
    /**
     * Add a student in a specified course.
     *
     * @param courseId - Id of the course.
     * @param enrollmentCode - Code of the course to enroll.
     * @return newly added student
     * @throws IOException - if credentials file not found.
     */
    public static Student addStudent(String courseId, String enrollmentCode)
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

        Student student = new Student().setUserId("me");
        try {
            // Enrolling a student to a specified course
            student = service.courses().students().create(courseId, student)
                    .setEnrollmentCode(enrollmentCode)
                    .execute();
            // Prints the course id with the Student name
            System.out.printf("User '%s' was enrolled as a student in the course with ID '%s'.\n",
                    student.getProfile().getName().getFullName(), courseId);
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 409) {
                System.out.println("You are already a member of this course.");
            } else if (error.getCode() == 403) {
                System.out.println("The caller does not have permission.\n");
            } else {
                throw e;
            }
        }
        return student;
    }
}
// [END classroom_add_student]