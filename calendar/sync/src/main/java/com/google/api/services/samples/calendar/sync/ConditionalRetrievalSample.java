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
 * A sample that demonstrates how to determine if a
 * <a href="https://developers.google.com/google-apps/calendar/v3/reference/">Calendar resource</a>
 * has been modified since you last retrieved it. It does this by passing along the etag of the
 * resource being retrieved in the "If-None-Match" HTTP header of the request, which will cause the
 * request to fail if the version on the server is the same as the local version.
 *
 * @author ekoleda+devrel@google.com (Eric Koleda)
 */
public class ConditionalRetrievalSample {

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
   * checks if the event has been modified.
   */
  private static void run() throws IOException {
    // Create a test event.
    Event event = Utils.createTestEvent(client, "Test Event");
    System.out.println(String.format("Event created: %s", event.getHtmlLink()));

    // Pause while the user modifies the event in the Calendar UI.
    System.out.println("Modify the event's description and hit enter to continue.");
    System.in.read();

    // Fetch the event again if it's been modified.
    Calendar.Events.Get getRequest = client.events().get("primary", event.getId());
    getRequest.setRequestHeaders(new HttpHeaders().setIfNoneMatch(event.getEtag()));
    try {
      event = getRequest.execute();
      System.out.println("The event was modified, retrieved latest version.");
    } catch (GoogleJsonResponseException e) {
      if (e.getStatusCode() == 304) {
        // A 304 status code, "Not modified", indicates that the etags match, and the event has
        // not been modified since we last retrieved it.
        System.out.println("The event was not modified, using local version.");
      } else {
        throw e;
      }
    }
  }
}
