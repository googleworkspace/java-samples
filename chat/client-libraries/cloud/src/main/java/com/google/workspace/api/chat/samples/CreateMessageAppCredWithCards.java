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
// [START chat_CreateMessageAppCredWithCards]
import com.google.apps.card.v1.Card;
import com.google.apps.card.v1.Card.CardHeader;
import com.google.apps.card.v1.Card.Section;
import com.google.apps.card.v1.TextParagraph;
import com.google.apps.card.v1.Widget;
import com.google.chat.v1.CardWithId;
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.CreateMessageRequest;
import com.google.chat.v1.Message;

// This sample shows how to create message with a card with app credential.
public class CreateMessageAppCredWithCards {

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithAppCredentials()) {
      CreateMessageRequest request =
        CreateMessageRequest.newBuilder()
        // Replace SPACE_NAME here.
        .setParent("spaces/SPACE_NAME")
        .setMessage(
            Message.newBuilder()
            .setText("Hello with app credentials!")
            .addCardsV2(
              CardWithId.newBuilder().setCard(
                Card.newBuilder()
                  .addSections(
                    Section.newBuilder()
                      .addWidgets(
                        Widget.newBuilder()
                          .setTextParagraph(
                            TextParagraph.newBuilder().setText("Hello"))
                          .build())
                      .build())
                  .build()
              )
            )
            .build())
        .build();
      Message response = chatServiceClient.createMessage(request);

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_CreateMessageAppCredWithCards]
