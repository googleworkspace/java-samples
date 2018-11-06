/*
 * Copyright (c) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.calendar.sync;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A collection of utility methods used by these samples.
 */
public class Utils {
  /** Application name */
  private static final String APPLICATION_NAME = "Calendar Sync Samples";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/calendar-sync");

  /** Global instance of the {@link DataStoreFactory}. */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  static {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  /** Creates a new Calendar client to use when making requests to the API. */
  public static Calendar createCalendarClient(List<String> scopes) throws Exception {
    Credential credential = authorize(scopes);
    return new Calendar.Builder(
        httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
  }

  /** Authorizes the installed application to access user's protected data. */
  public static Credential authorize(List<String> scopes) throws Exception {
    // Load client secrets.
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(SyncTokenSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter")) {
      System.out.println(
          "Overwrite the src/main/resources/client_secrets.json file with the client secrets file "
          + "you downloaded from your Google Developers Console project.");
      System.exit(1);
    }

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
        JSON_FACTORY, clientSecrets, scopes).setDataStoreFactory(dataStoreFactory).build();
    // Authorize.
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  /** Gets the datastore factory used in these samples. */
  public static DataStoreFactory getDataStoreFactory() {
    return dataStoreFactory;
  }

  /** Creates a test event. */
  public static Event createTestEvent(Calendar client, String summary) throws IOException {
    Date oneHourFromNow = Utils.getRelativeDate(java.util.Calendar.HOUR, 1);
    Date twoHoursFromNow = Utils.getRelativeDate(java.util.Calendar.HOUR, 2);
    DateTime start = new DateTime(oneHourFromNow, TimeZone.getTimeZone("UTC"));
    DateTime end = new DateTime(twoHoursFromNow, TimeZone.getTimeZone("UTC"));

    Event event = new Event().setSummary(summary)
        .setReminders(new Reminders().setUseDefault(false))
        .setStart(new EventDateTime().setDateTime(start))
        .setEnd(new EventDateTime().setDateTime(end));
    return client.events().insert("primary", event).execute();
  }

  /**
   * Gets a new {@link java.util.Date} relative to the current date and time.
   *
   * @param field the field identifier from {@link java.util.Calendar} to increment
   * @param amount the amount of the field to increment
   * @return the new date
   */
  public static Date getRelativeDate(int field, int amount) {
    Date now = new Date();
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.setTime(now);
    cal.add(field, amount);
    return cal.getTime();
  }
}
