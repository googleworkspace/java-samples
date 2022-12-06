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


// [START classroom_list_topic]

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.ListTopicResponse;
import com.google.api.services.classroom.model.Topic;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate how to list topics in a course. */
public class ListTopics {
  /**
   * List topics in a course.
   *
   * @param courseId - the id of the course to retrieve topics for.
   * @return - the list of topics in the course that the caller is permitted to view.
   * @throws IOException - if credentials file not found.
   */
  public static List<Topic> listTopics(String courseId) throws IOException {
    /* Load pre-authorized user credentials from the environment.
     TODO(developer) - See https://developers.google.com/identity for
      guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_TOPICS));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client.
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Classroom samples")
        .build();

    List<Topic> topics = new ArrayList<>();
    String pageToken = null;

    try {
      do {
        ListTopicResponse response = service.courses().topics().list(courseId)
            .setPageSize(100)
            .setPageToken(pageToken)
            .execute();
        topics.addAll(response.getTopic());
        pageToken = response.getNextPageToken();
      } while (pageToken != null);

      if (topics.isEmpty()) {
        System.out.println("No topics found.");
      } else {
        for (Topic topic : topics) {
          System.out.printf("%s (%s)\n", topic.getName(), topic.getTopicId());
        }
      }
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The courseId does not exist: %s.\n", courseId);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return topics;
  }

}
