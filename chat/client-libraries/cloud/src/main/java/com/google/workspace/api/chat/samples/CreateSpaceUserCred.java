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
// [START chat_create_space_user_cred]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.CreateSpaceRequest;
import com.google.chat.v1.Space;

// This sample shows how to create space with user credential.
public class CreateSpaceUserCred {

  private static final String SCOPE =
    "https://www.googleapis.com/auth/chat.spaces.create";

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithUserCredentials(
          ImmutableList.of(SCOPE))) {
      CreateSpaceRequest.Builder request = CreateSpaceRequest.newBuilder()
        .setSpace(Space.newBuilder()
          .setSpaceType(Space.SpaceType.SPACE)
          // Replace DISPLAY_NAME here.
          .setDisplayName("DISPLAY_NAME"));
      Space response = chatServiceClient.createSpace(request.build());

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_create_space_user_cred]
