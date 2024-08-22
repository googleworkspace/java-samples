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
// [START chat_create_membership_user_cred_for_app]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.CreateMembershipRequest;
import com.google.chat.v1.Membership;
import com.google.chat.v1.SpaceName;
import com.google.chat.v1.User;

// This sample shows how to create membership with user credential for the
// calling app.
public class CreateMembershipUserCredForApp {

  private static final String SCOPE =
    "https://www.googleapis.com/auth/chat.memberships.app";

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithUserCredentials(
          ImmutableList.of(SCOPE))) {
      CreateMembershipRequest.Builder request = CreateMembershipRequest.newBuilder()
        // replace SPACE_NAME here
        .setParent("spaces/SPACE_NAME")
        .setMembership(Membership.newBuilder()
          .setMember(User.newBuilder()
            // member name for app membership, do not change this.
            .setName("users/app")
            // user type for the membership
            .setType(User.Type.BOT)));
      Membership response = chatServiceClient.createMembership(request.build());

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_create_membership_user_cred_for_app]
