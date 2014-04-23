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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStore;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A sample that demonstrates how to efficiently sync
 * <a href="https://developers.google.com/google-apps/calendar/v3/reference/">Calendar resource</a>
 * using sync tokens.
 *
 * @author ekoleda+devrel@google.com (Eric Koleda)
 */
public class SyncTokenSample {

  /** Global instance of the Calendar client. */
  private static Calendar client;

  /** Global instance of the event datastore. */
  private static DataStore<String> eventDataStore;

  /** Global instance of the sync settings datastore. */
  private static DataStore<String> syncSettingsDataStore;

  /** The key in the sync settings datastore that holds the current sync token. */
  private static final String SYNC_TOKEN_KEY = "syncToken";

  public static void main(String[] args) {
    try {
      List<String> scopes = Lists.newArrayList(CalendarScopes.CALENDAR_READONLY);
      client = Utils.createCalendarClient(scopes);
      eventDataStore = Utils.getDataStoreFactory().getDataStore("EventStore");
      syncSettingsDataStore = Utils.getDataStoreFactory().getDataStore("SyncSettings");
      run();
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Syncs events from the user's primary calendar to a local datastore. A full sync is performed on
   * the first run, with incremental syncs on subsequent runs.
   */
  private static void run() throws IOException {
    // Construct the {@link Calendar.Events.List} request, but don't execute it yet.
    Calendar.Events.List request = client.events().list("primary");

    // Load the sync token stored from the last execution, if any.
    String syncToken = syncSettingsDataStore.get(SYNC_TOKEN_KEY);
    if (syncToken == null) {
      System.out.println("Performing full sync.");

      // Set the filters you want to use during the full sync. Sync tokens aren't compatible with
      // most filters, but you may want to limit your full sync to only a certain date range.
      // In this example we are only syncing events up to a year old.
      Date oneYearAgo = Utils.getRelativeDate(java.util.Calendar.YEAR, -1);
      request.setTimeMin(new DateTime(oneYearAgo, TimeZone.getTimeZone("UTC")));
    } else {
      System.out.println("Performing incremental sync.");
      request.setSyncToken(syncToken);
    }

    // Retrieve the events, one page at a time.
    String pageToken = null;
    Events events = null;
    do {
      request.setPageToken(pageToken);

      try {
        events = request.execute();
      } catch (GoogleJsonResponseException e) {
        if (e.getStatusCode() == 410) {
          // A 410 status code, "Gone", indicates that the sync token is invalid.
          System.out.println("Invalid sync token, clearing event store and re-syncing.");
          syncSettingsDataStore.delete(SYNC_TOKEN_KEY);
          eventDataStore.clear();
          run();
        } else {
          throw e;
        }
      }

      List<Event> items = events.getItems();
      if (items.size() == 0) {
        System.out.println("No new events to sync.");
      } else {
        for (Event event : items) {
          syncEvent(event);
        }
      }

      pageToken = events.getNextPageToken();
    } while (pageToken != null);

    // Store the sync token from the last request to be used during the next execution.
    syncSettingsDataStore.set(SYNC_TOKEN_KEY, events.getNextSyncToken());

    System.out.println("Sync complete.");
  }

  /**
   * Sync an individual event. In this example we simply store it's string represenation to a file
   * system data store.
   */
  private static void syncEvent(Event event) throws IOException {
    if ("cancelled".equals(event.getStatus()) && eventDataStore.containsKey(event.getId())) {
      eventDataStore.delete(event.getId());
      System.out.println(String.format("Deleting event: ID=%s", event.getId()));
    } else {
      eventDataStore.set(event.getId(), event.toString());
      System.out.println(
          String.format("Syncing event: ID=%s, Name=%s", event.getId(), event.getSummary()));
    }
  }
}
