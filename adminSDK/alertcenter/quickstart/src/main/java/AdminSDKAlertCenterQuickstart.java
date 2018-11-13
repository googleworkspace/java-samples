// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START admin_sdk_alertcenter_quickstart]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.alertcenter.v1beta1.AlertCenter;
import com.google.api.services.alertcenter.v1beta1.model.Alert;
import com.google.api.services.alertcenter.v1beta1.model.AlertFeedback;
import com.google.api.services.alertcenter.v1beta1.model.ListAlertsResponse;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class AdminSDKAlertCenterQuickstart {

  private static final String APPLICATION_NAME = "Google Admin SDK Alert Center API Java Quickstart";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
   * your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Collections
      .singletonList("https://www.googleapis.com/auth/apps.alerts");
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";


  /**
   * Creates an authorized Credentials object.
   *
   * @return An authorized Credentials object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credentials getCredentials(String delegatedAdmin) throws IOException {
    // [START admin_sdk_alertcenter_get_credentials]
    InputStream in = AdminSDKAlertCenterQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new IOException("Credential file was not found");
    }
    GoogleCredentials credentials = ServiceAccountCredentials
        .fromStream(in)
        .createDelegated(delegatedAdmin)
        .createScoped(SCOPES);
    // [END admin_sdk_alertcenter_get_credentials]
    return credentials;
  }

  public static void main(String... args) throws IOException, GeneralSecurityException {
    // [START admin_sdk_alertcenter_create_client]
    String delegatedAdmin = "admin@xxx.com";
    NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
    AlertCenter service = new AlertCenter.Builder(transport, JSON_FACTORY,
        new HttpCredentialsAdapter(getCredentials(delegatedAdmin)))
        .setApplicationName(APPLICATION_NAME)
        .build();
    // [END admin_sdk_alertcenter_create_client]

    // [START admin_sdk_alertcenter_list_alerts]
    ListAlertsResponse listResponse = service.alerts().list().setPageSize(20).execute();
    while (listResponse.getAlerts() != null) {
      for (Alert alert : listResponse.getAlerts()) {
        System.out.println(alert);
      }
      if (listResponse.getNextPageToken() == null || listResponse.getNextPageToken().isEmpty()) {
        break;
      }
      listResponse = service.alerts().list().setPageToken(listResponse.getNextPageToken())
          .setPageSize(20).execute();
    }
    // [END admin_sdk_alertcenter_list_alerts]


    listResponse = service.alerts().list().setPageSize(20).execute();
    if (listResponse == null || listResponse.isEmpty()) {
      System.out.println("No alerts");
    }
    String alertId = listResponse.getAlerts().get(0).getAlertId();
    // [START admin_sdk_alertcenter_provide_feedback]
    AlertFeedback newFeedback = new AlertFeedback();
    newFeedback.setType("VERY_USEFUL");
    AlertFeedback feedback = service.alerts().feedback().create(alertId, newFeedback).execute();
    System.out.println(feedback);
    // [END admin_sdk_alertcenter_provide_feedback]
  }
}
// [END admin_sdk_alertcenter_quickstart]
