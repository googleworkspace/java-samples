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

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.chat.v1.ChatServiceClient;
import com.google.chat.v1.ChatServiceSettings;
import com.google.common.collect.ImmutableList;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Collections;
import java.util.Date;

public class AuthenticationUtils{

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String SERVICE_ACCOUNT_FILE = "service_account.json";
  private static final String CLIENT_SECRET_FILE = "client_secrets.json";
  private static final String APP_AUTH_OAUTH_SCOPE =
    "https://www.googleapis.com/auth/chat.bot";

  public static ChatServiceClient createClientWithAppCredentials()
      throws Exception {
    // For more information on service account authentication, see
    // https://developers.google.com/workspace/chat/authenticate-authorize-chat-app
    GoogleCredentials credential = ServiceAccountCredentials.fromStream(
        new FileInputStream(SERVICE_ACCOUNT_FILE))
      .createScoped(ImmutableList.of(APP_AUTH_OAUTH_SCOPE));

    // Create the ChatServiceSettings with the app credentials
    ChatServiceSettings chatServiceSettings =
      ChatServiceSettings.newBuilder()
      .setCredentialsProvider(
          FixedCredentialsProvider.create(credential))
      .build();

    return ChatServiceClient.create(chatServiceSettings);
  }

  public static ChatServiceClient createClientWithUserCredentials(
      ImmutableList<String> scopes) throws Exception {
    // For more information on user authentication, see
    // https://developers.google.com/workspace/chat/authenticate-authorize-chat-user
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new FileReader(CLIENT_SECRET_FILE));

    Credential credential = authorize(scopes, clientSecrets);

    AccessToken accessToken = new AccessToken(
        credential.getAccessToken(),
        new Date(
          // put the actual expiry date of access token here
          System.currentTimeMillis()));
    UserCredentials googleCredentials =
      UserCredentials
        .newBuilder()
        .setAccessToken(accessToken)
        .setRefreshToken(credential.getRefreshToken())
        .setClientId(clientSecrets.getInstalled().getClientId())
        .setClientSecret(clientSecrets.getInstalled().getClientSecret())
        .build();

    // Create the ChatServiceSettings with the credentials
    ChatServiceSettings chatServiceSettings =
      ChatServiceSettings
        .newBuilder()
        .setCredentialsProvider(
          FixedCredentialsProvider.create(googleCredentials))
        .build();

    return ChatServiceClient.create(chatServiceSettings);
  }

  // Generate access token and refresh token using scopes and client secrets.
  private static Credential authorize(
      ImmutableList<String> scopes, GoogleClientSecrets clientSecrets)
      throws Exception {
    // Set up authorization code flow.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JSON_FACTORY,
        clientSecrets,
        scopes)
      // Set these two options to generate refresh token alongside access token.
      .setAccessType("offline")
      .setApprovalPrompt("force")
      .build();

    // Authorize.
    return new AuthorizationCodeInstalledApp(
        flow, new LocalServerReceiver()).authorize("user");
  }
}
