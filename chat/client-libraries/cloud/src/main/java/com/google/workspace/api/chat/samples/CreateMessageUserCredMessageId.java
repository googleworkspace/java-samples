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
import com.google.protobuf.util.JsonFormat;
// [START chat_create_message_user_cred_message_id]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.CreateMessageRequest;
import com.google.chat.v1.Message;

// This sample shows how to create message with message id specified with user
// credential.
public class CreateMessageUserCredMessageId {

  private static final String SCOPE =
    "https://www.googleapis.com/auth/chat.messages.create";

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithUserCredentials(
          ImmutableList.of(SCOPE))) {
      CreateMessageRequest.Builder request = CreateMessageRequest.newBuilder()
        // Replace SPACE_NAME here.
        .setParent("spaces/SPACE_NAME")
        .setMessage(Message.newBuilder()
          .setText("Hello with user credentials!"))
        // Message ID lets chat apps get, update or delete a message without
        // needing to store the system assigned ID in the message's resource
        // name.
        .setMessageId("client-MESSAGE-ID");
      Message response = chatServiceClient.createMessage(request.build());

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_create_message_user_cred_message_id]
