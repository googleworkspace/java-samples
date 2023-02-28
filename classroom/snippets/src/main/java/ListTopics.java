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

// [START classroom_list_topic_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.ListTopicResponse;
import com.google.api.services.classroom.model.Topic;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate how to list topics in a course. */
public class ListTopics {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES =
      new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_TOPICS));

  /**
   * List topics in a course.
   *
   * @param courseId - the id of the course to retrieve topics for.
   * @return - the list of topics in the course that the caller is permitted to view.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static List<Topic> listTopics(String courseId)
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

    // [START classroom_list_topic_code_snippet]

    List<Topic> topics = new ArrayList<>();
    String pageToken = null;

    try {
      do {
        ListTopicResponse response =
            service
                .courses()
                .topics()
                .list(courseId)
                .setPageSize(100)
                .setPageToken(pageToken)
                .execute();

        /* Ensure that the response is not null before retrieving data from it to avoid errors. */
        if (response.getTopic() != null) {
          topics.addAll(response.getTopic());
          pageToken = response.getNextPageToken();
        }
      } while (pageToken != null);

      if (topics.isEmpty()) {
        System.out.println("No topics found.");
      } else {
        for (Topic topic : topics) {
          System.out.printf("%s (%s)\n", topic.getName(), topic.getTopicId());
        }
      }
    } catch (GoogleJsonResponseException e) {
      // TODO (developer) - handle error appropriately
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

    // [END classroom_list_topic_code_snippet]

  }
}
// [END classroom_list_topic_class]
