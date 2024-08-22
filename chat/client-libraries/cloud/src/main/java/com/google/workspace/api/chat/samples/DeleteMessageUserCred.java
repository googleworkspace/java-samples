/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.workspace.api.chat.samples;

import com.google.common.collect.ImmutableList;
// [START chat_delete_message_user_cred]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.DeleteMessageRequest;
import com.google.chat.v1.SpaceName;

// This sample shows how to delete message with user credential.
public class DeleteMessageUserCred {

  private static final String SCOPE =
    "https://www.googleapis.com/auth/chat.messages";

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithUserCredentials(
          ImmutableList.of(SCOPE))) {
      DeleteMessageRequest.Builder request = DeleteMessageRequest.newBuilder()
        // replace SPACE_NAME and MESSAGE_NAME here
        .setName("spaces/SPACE_NAME/messages/MESSAGE_NAME");
      chatServiceClient.deleteMessage(request.build());
    }
  }
}
// [END chat_delete_message_user_cred]
