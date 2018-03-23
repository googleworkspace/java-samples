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
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

/**
 * A sample that demonstrates how to update a
 * <a href="https://developers.google.com/google-apps/calendar/v3/reference/">Calendar resource</a>
 * safely, ensuring that other changes aren't overwritten. It does this by passing along the etag of
 * the resource being updated in the "If-Match" HTTP header of the request, which will cause the
 * request to fail if the version on the server is different.
 *
 * @author ekoleda+devrel@google.com (Eric Koleda)
 */
public class ConditionalModificationSample {

  /** The maximum number of times to attempt to update the event, before aborting. */
  private static final int MAX_UPDATE_ATTEMPTS = 5;

  /** Global instance of the Calendar client. */
  private static Calendar client;

  public static void main(String[] args) {
    try {
      List<String> scopes = Lists.newArrayList(CalendarScopes.CALENDAR);
      client = Utils.createCalendarClient(scopes);
      run();
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Creates a test event, pauses while the user modifies the event in the Calendar UI, and then
   * updates the event with a new location, ensure that the user's changes aren't overwritten.
   */
  private static void run() throws IOException {
    // Create a test event.
    Event event = Utils.createTestEvent(client, "Test Event");
    System.out.println(String.format("Event created: %s", event.getHtmlLink()));

    // Pause while the user modifies the event in the Calendar UI.
    System.out.println("Modify the event's description and hit enter to continue.");
    System.in.read();

    // Modify the local copy of the event.
    event.setSummary("Updated Test Event");

    // Update the event, making sure that we don't overwrite other changes.
    int numAttempts = 0;
    boolean isUpdated = false;
    do {
      Calendar.Events.Update request = client.events().update("primary", event.getId(), event);
      request.setRequestHeaders(new HttpHeaders().setIfMatch(event.getEtag()));
      try {
        event = request.execute();
        isUpdated = true;
      } catch (GoogleJsonResponseException e) {
        if (e.getStatusCode() == 412) {
          // A 412 status code, "Precondition failed", indicates that the etag values didn't
          // match, and the event was updated on the server since we last retrieved it. Use
          // {@link Calendar.Events.Get} to retrieve the latest version.
          Event latestEvent = client.events().get("primary", event.getId()).execute();

          // You may want to have more complex logic here to resolve conflicts. In this sample we're
          // simply overwriting the summary.
          latestEvent.setSummary(event.getSummary());
          event = latestEvent;
        } else {
          throw e;
        }
      }
      numAttempts++;
    } while (!isUpdated && numAttempts <= MAX_UPDATE_ATTEMPTS);

    if (isUpdated) {
      System.out.println("Event updated.");
    } else {
      System.out.println(String.format("Failed to update event after %d attempts.", numAttempts));
    }
  }
}
