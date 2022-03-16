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


// [START classroom_list_courses]
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Classroom List Course API */
public class ListCourses {
    /**
     * Retrieves all courses with metadata
     *
     * @return list of courses with its metadata
     * @throws IOException - if credentials file not found.
     */
    public static List<Course> listCourses() throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_COURSES));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the classroom API client
        Classroom service = new Classroom.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Classroom samples")
                .build();

        String pageToken = null;
        List<Course> courses = new ArrayList<>();

        try {
            do {
                ListCoursesResponse response = service.courses().list()
                        .setPageSize(100)
                        .setPageToken(pageToken)
                        .execute();
                courses.addAll(response.getCourses());
                pageToken = response.getNextPageToken();
            } while (pageToken != null);

            if (courses.isEmpty()) {
                System.out.println("No courses found.");
            } else {
                System.out.println("Courses:");
                for (Course course : courses) {
                    System.out.printf("%s (%s)\n", course.getName(), course.getId());
                }
            }
        } catch (NullPointerException ne) {
            // TODO(developer) - handle error appropriately
            System.err.println("No courses found.\n");
        }
        return courses;
    }
}
// [END classroom_list_courses]