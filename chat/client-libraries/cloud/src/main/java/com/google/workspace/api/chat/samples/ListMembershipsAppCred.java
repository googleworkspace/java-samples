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

import com.google.protobuf.util.JsonFormat;
// [START chat_list_memberships_app_cred]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.ListMembershipsRequest;
import com.google.chat.v1.ListMembershipsResponse;
import com.google.chat.v1.Membership;

// This sample shows how to list memberships with app credential.
public class ListMembershipsAppCred {

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithAppCredentials()) {
      ListMembershipsRequest.Builder request = ListMembershipsRequest.newBuilder()
        // Replace SPACE_NAME here.
        .setParent("spaces/SPACE_NAME")
        // Filter membership by type (HUMAN or BOT) or role
        // (ROLE_MEMBER or ROLE_MANAGER).
        .setFilter("member.type = \"HUMAN\"")
        // Number of results that will be returned at once.
        .setPageSize(10);

      // Iterate over results and resolve additional pages automatically.
      for (Membership response :
          chatServiceClient.listMemberships(request.build()).iterateAll()) {
        System.out.println(JsonFormat.printer().print(response));
      }
    }
  }
}
// [END chat_list_memberships_app_cred]
