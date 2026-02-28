// Copyright 2026 Google LLC
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

// [START gmail_list_messages]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/*
 * Class to demonstrate the use of Gmail List Messages API
 */
public class ListMessages {

  /**
   * Lists the user's Gmail messages.
   *
   * @param service Authorized Gmail API instance.
   * @return List of Messages in the user's mailbox.
   * @throws IOException if the request to retrieve messages fails.
   * @throws GoogleJsonResponseException if the execution of the request fails.
   */
  public static List<Message> listMessages(Gmail service) throws IOException {

    try {
      final long maxMessages = 10L;
      final String userId = "me";
      ListMessagesResponse results =
          service
              .users()
              .messages()
              .list(userId)
              .setLabelIds(Collections.singletonList("INBOX"))
              .setMaxResults(maxMessages)
              .execute();

      List<Message> messages = results.getMessages();

      if (messages == null || messages.isEmpty()) {
        System.out.println("No messages found.");
        return messages;
      }

      System.out.println("Messages:");
      for (Message message : messages) {
        /*TODO(developer) - Consider batch requests if you need to retrieve many messages at once.
        See https://developers.google.com/workspace/gmail/api/guides/batch for implementation guides. */
        Message msg = service.users().messages().get(userId, message.getId()).execute();

        System.out.printf("- Message ID: %s%n  Snippet: %s%n", message.getId(), msg.getSnippet());
      }
      return messages;
    } catch (GoogleJsonResponseException e) {
      // TODO(developer) - handle error appropriately
      System.err.println("An error occurred: " + e);
      throw e;
    }
  }

  private static Gmail getGmailService() throws IOException, GeneralSecurityException {
    /* Load pre-authorized user credentials from the environment.
    TODO(developer) - See https://developers.google.com/identity for
     guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials =
        GoogleCredentials.getApplicationDefault().createScoped(GmailScopes.GMAIL_READONLY);
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

    // Create the gmail API client
    return new Gmail.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer)
        .setApplicationName("Gmail samples")
        .build();
  }
}

// [END gmail_list_messages]
