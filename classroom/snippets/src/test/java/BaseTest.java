/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseAlias;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.UUID;

 // Base class for integration tests.
public class BaseTest {
     protected Classroom service;
     protected Course testCourse;

    /**
     * Creates a default authorization Classroom client service.
     *
     * @return an authorized Classroom client service
     * @throws IOException - if credentials file not found.
     */
    protected Classroom buildService() throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(ClassroomScopes.CLASSROOM_ROSTERS);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the classroom API client
        Classroom service = new Classroom.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Classroom Snippets")
                .build();

        return service;
    }

    @Before
    public void setup() throws IOException{
        this.service = buildService();
        this.testCourse = CreateCourse.createCourse();
        createAlias(this.testCourse.getId());
    }

    @After
    public void tearDown() throws IOException{
        deleteCourse(this.testCourse.getId());
        this.testCourse = null;
    }

    public CourseAlias createAlias(String courseId) throws IOException {
        String alias = "p:" + UUID.randomUUID();
        CourseAlias courseAlias = new CourseAlias().setAlias(alias);
        courseAlias = this.service.courses().aliases().create(courseId, courseAlias).execute();
        return courseAlias;
    }

     public void deleteCourse(String courseId) throws IOException {
         this.service.courses().delete(courseId).execute();
     }
}
