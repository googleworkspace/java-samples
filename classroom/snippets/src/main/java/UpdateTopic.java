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


// [START classroom_update_topic]

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Topic;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate how to update one or more fields in a topic. */
public class UpdateTopic {
  /**
   * Update one or more fields in a topic in a course.
   *
   * @param courseId - the id of the course where the topic belongs.
   * @param topicId - the id of the topic to update.
   * @return - updated topic.
   * @throws IOException - if credentials file not found.
   */
  public static Topic updateTopic(String courseId, String topicId) throws IOException {
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

    Topic topic = null;
    try {
      // Retrieve the topic to update.
      Topic topicToUpdate = service.courses().topics().get(courseId, topicId).execute();

      // Update the name field for the topic retrieved.
      topicToUpdate.setName("Semester 2");

      /* Call the patch endpoint and set the updateMask query parameter to the field that needs to
       be updated. */
      topic = service.courses().topics().patch(courseId, topicId, topicToUpdate)
          .set("updateMask", "name")
          .execute();
    } catch(GoogleJsonResponseException e) {
      // TODO(developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The courseId or topicId does not exist: %s, %s.\n", courseId, topicId);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return topic;
  }
}
