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

import java.util.List;

// [START chat_set_up_space_user_cred]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.Membership;
import com.google.chat.v1.SetUpSpaceRequest;
import com.google.chat.v1.Space;
import com.google.chat.v1.User;

// This sample shows how to set up a named space with one initial member with
// user credential.
public class SetUpSpaceUserCred {

  private static final String SCOPE =
    "https://www.googleapis.com/auth/chat.spaces.create";

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithUserCredentials(
          ImmutableList.of(SCOPE))) {
      SetUpSpaceRequest.Builder request = SetUpSpaceRequest.newBuilder()
        .setSpace(Space.newBuilder()
          .setSpaceType(Space.SpaceType.SPACE)
          // Replace DISPLAY_NAME here.
          .setDisplayName("DISPLAY_NAME"))
        .addAllMemberships(List.of(Membership.newBuilder()
          .setMember(User.newBuilder()
            // Replace USER_NAME here.
            .setName("users/USER_NAME")
            .setType(User.Type.HUMAN)).build()));
      Space response = chatServiceClient.setUpSpace(request.build());

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_set_up_space_user_cred]
