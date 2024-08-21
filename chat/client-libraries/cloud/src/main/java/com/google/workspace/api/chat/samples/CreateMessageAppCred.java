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
// [START chat_CreateMessageAppCred]
import com.google.apps.card.v1.Button;
import com.google.apps.card.v1.ButtonList;
import com.google.apps.card.v1.Card;
import com.google.apps.card.v1.Icon;
import com.google.apps.card.v1.MaterialIcon;
import com.google.apps.card.v1.OnClick;
import com.google.apps.card.v1.OpenLink;
import com.google.apps.card.v1.TextParagraph;
import com.google.apps.card.v1.Widget;
import com.google.apps.card.v1.Card.CardHeader;
import com.google.apps.card.v1.Card.Section;
import com.google.chat.v1.AccessoryWidget;
import com.google.chat.v1.CardWithId;
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.CreateMessageRequest;
import com.google.chat.v1.Message;

// This sample shows how to create message with app credential.
public class CreateMessageAppCred {

  public static void main(String[] args) throws Exception {
    try (ChatServiceClient chatServiceClient =
        AuthenticationUtils.createClientWithAppCredentials()) {
      CreateMessageRequest request =
        CreateMessageRequest.newBuilder()
        // Replace SPACE_NAME here.
        .setParent("spaces/SPACE_NAME")
        .setMessage(Message.newBuilder()
          .setText( "👋🌎 Hello world! I created this message by calling " +
                    "the Chat API\'s `messages.create()` method.")
          .addCardsV2(CardWithId.newBuilder().setCard(Card.newBuilder()
            .setHeader(CardHeader.newBuilder()
              .setTitle("About this message")
              .setImageUrl("https://fonts.gstatic.com/s/i/short-term/release/googlesymbols/info/default/24px.svg").build())
            .addSections(Section.newBuilder()
              .setHeader("Contents")
              .addWidgets(Widget.newBuilder().setTextParagraph(TextParagraph.newBuilder().setText(
                "🔡 <b>Text</b> which can include " +
                "hyperlinks 🔗, emojis 😄🎉, and @mentions 🗣️."))
                .build())
              .addWidgets(Widget.newBuilder().setTextParagraph(TextParagraph.newBuilder().setText(
                "🖼️ A <b>card</b> to display visual elements " +
                "and request information such as text 🔤, " +
                "dates and times 📅, and selections ☑️."))
                .build())
              .addWidgets(Widget.newBuilder().setTextParagraph(TextParagraph.newBuilder().setText(
                "👉🔘 An <b>accessory widget</b> which adds " +
                "a button to the bottom of a message."))
                .build())
              .build())
            .addSections(Section.newBuilder()
              .setHeader("What's next")
              .setCollapsible(true)
              .addWidgets(Widget.newBuilder().setTextParagraph(TextParagraph.newBuilder().setText(
                "❤️ <a href='https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces.messages.reactions/create'>Add a reaction</a>."))
                .build())
              .addWidgets(Widget.newBuilder().setTextParagraph(TextParagraph.newBuilder().setText(
                "🔄 <a href='https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces.messages/patch'>Update</a> " +
                "or ❌ <a href='https://developers.google.com/workspace/chat/api/reference/rest/v1/spaces.messages/delete'>delete</a> " +
                "the message."))
                .build())
              .build())
            .build()))
          .addAccessoryWidgets(AccessoryWidget.newBuilder()
            .setButtonList(ButtonList.newBuilder()
              .addButtons(Button.newBuilder()
                .setText("View documentation")
                .setIcon(Icon.newBuilder()
                  .setMaterialIcon(MaterialIcon.newBuilder().setName("link").build())
                  .build())
                .setOnClick(OnClick.newBuilder()
                  .setOpenLink(OpenLink.newBuilder()
                    .setUrl("https://developers.google.com/workspace/chat/create-messages")
                    .build())
                  .build())
                .build())
              .build())
            .build())
          .build())
        .build();
      Message response = chatServiceClient.createMessage(request);

      System.out.println(JsonFormat.printer().print(response));
    }
  }
}
// [END chat_CreateMessageAppCred]
