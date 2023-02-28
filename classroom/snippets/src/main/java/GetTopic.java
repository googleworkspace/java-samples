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

// [START classroom_get_topic_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Topic;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/* Class to demonstrate how to get a topic. */
public class GetTopic {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES =
      new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_TOPICS));

  /**
   * Get a topic in a course.
   *
   * @param courseId - the id of the course the topic belongs to.
   * @param topicId - the id of the topic to retrieve.
   * @return - the topic to retrieve.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static Topic getTopic(String courseId, String topicId)
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

    // [START classroom_get_topic_code_snippet]

    Topic topic = null;
    try {
      // Get the topic.
      topic = service.courses().topics().get(courseId, topicId).execute();
      System.out.printf("Topic '%s' found.\n", topic.getName());
    } catch (GoogleJsonResponseException e) {
      // TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The courseId or topicId does not exist: %s, %s.\n", courseId, topicId);
      }
      throw e;
    } catch (Exception e) {
      throw e;
    }
    return topic;

    // [END classroom_get_topic_code_snippet]

  }
}
// [END classroom_get_topic_class]
