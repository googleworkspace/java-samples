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
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
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
import java.util.List;

/* Class to demonstrate the use of Classroom Batch Add Students API */
public class BatchAddStudents {
    /**
     * Add multiple students in a specified course.
     *
     * @param courseId - Id of the course to add students.
     * @param studentEmails - Email address of the students.
     * @throws IOException - if credentials file not found.
     */
    public static void batchAddStudents(String courseId, List<String> studentEmails)
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

        BatchRequest batch = service.batch();
        JsonBatchCallback<Student> callback = new JsonBatchCallback<>() {
            public void onSuccess(Student student, HttpHeaders responseHeaders) {
                System.out.printf("User '%s' was added as a student to the course.\n",
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