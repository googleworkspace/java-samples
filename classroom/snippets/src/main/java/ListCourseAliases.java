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


// [START classroom_list_aliases]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.CourseAlias;
import com.google.api.services.classroom.model.ListCourseAliasesResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Classroom List Alias API */
public class ListCourseAliases {
    /**
     * Retrieve the aliases for a course.
     *
     * @param courseId - id of the course.
     * @return list of course aliases
     * @throws IOException - if credentials file not found.
     */
    public static List<CourseAlias> listCourseAliases(String courseId)
            throws IOException {
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
    }
}
// [END classroom_list_aliases]