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
// [START chat_get_message_user_cred]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.GetMessageRequest;
import com.google.chat.v1.Message;

// This sample shows how to get message with user credential.
public class GetMessageUserCred {

  private static final String SCOPE =
    "https://www.googleapis.com/auth/chat.messages.readonly";

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithUserCredentials(
          ImmutableList.of(SCOPE))) {
      GetMessageRequest.Builder request = GetMessageRequest.newBuilder()
        // replace SPACE_NAME and MESSAGE_NAME here
        .setName("spaces/SPACE_NAME/members/MESSAGE_NAME");
      Message response = chatServiceClient.getMessage(request.build());

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_get_message_user_cred]
