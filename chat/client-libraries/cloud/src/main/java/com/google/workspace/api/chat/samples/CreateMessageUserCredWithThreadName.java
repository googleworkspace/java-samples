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
// [START chat_CreateMessageUserCredWithThreadName]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.CreateMessageRequest;
import com.google.chat.v1.CreateMessageRequest.MessageReplyOption;
import com.google.chat.v1.Message;
import com.google.chat.v1.Thread;

// This sample shows how to create message with thread name specified with user
// credential.
public class CreateMessageUserCredWithThreadName {

  private static final String SCOPE =
    "https://www.googleapis.com/auth/chat.messages.create";

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithUserCredentials(
          ImmutableList.of(SCOPE))) {
      CreateMessageRequest.Builder request = CreateMessageRequest.newBuilder()
        // Replace SPACE_NAME here.
        .setParent("spaces/SPACE_NAME")
        // Creates the message as a reply to the thread specified by thread name.
        // If it fails, the message starts a new thread instead.
        .setMessageReplyOption(
          MessageReplyOption.REPLY_MESSAGE_FALLBACK_TO_NEW_THREAD)
        .setMessage(Message.newBuilder()
          .setText("Hello with user credentials!")
          // Resource name of a thread that uniquely identify a thread.
          .setThread(Thread.newBuilder().setName(
            // replace SPACE_NAME and THREAD_NAME here
            "spaces/SPACE_NAME/threads/THREAD_NAME")));
      Message response = chatServiceClient.createMessage(request.build());

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_CreateMessageUserCredWithThreadName]
