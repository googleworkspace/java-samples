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
// [START chat_list_spaces_app_cred]
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.ListSpacesRequest;
import com.google.chat.v1.ListSpacesResponse;
import com.google.chat.v1.Space;

// This sample shows how to list spaces with app credential.
public class ListSpacesAppCred {

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithAppCredentials()) {
      ListSpacesRequest.Builder request = ListSpacesRequest.newBuilder()
        // Filter spaces by space type (SPACE or GROUP_CHAT or
        // DIRECT_MESSAGE).
        .setFilter("spaceType = \"SPACE\"")
        // Number of results that will be returned at once.
        .setPageSize(10);

      // Iterate over results and resolve additional pages automatically.
      for (Space response :
          chatServiceClient.listSpaces(request.build()).iterateAll()) {
        System.out.println(JsonFormat.printer().print(response));
      }
    }
  }
}
// [END chat_list_spaces_app_cred]
