// Copyright 2026 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

// Unit test case for gmail list messages snippet
public class TestListMessages {

  @Test
  public void testListMessagesReturnsMessages() throws IOException {
    HttpTransport transport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            return new MockLowLevelHttpRequest() {
              @Override
              public LowLevelHttpResponse execute() {
                MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                response.setStatusCode(200);
                response.setContentType(Json.MEDIA_TYPE);

                // Mock response for listing messages in the inbox
                if (url.contains("/messages") && url.contains("labelIds=INBOX")) {
                  response.setContent("{\"messages\": [{\"id\": \"12345\"}]}");
                } else if (url.contains("/messages/12345")) {
                  response.setContent(
                      "{\"id\": \"12345\", \"snippet\": \"This is a test snippet.\"}");
                } else {
                  response.setStatusCode(404);
                }
                return response;
              }
            };
          }
        };

    Gmail service = getMockGmailService(transport);
    List<Message> result = ListMessages.listMessages(service);

    assertNotNull("The returned message list should not be null", result);
    assertEquals("The returned list should contain exactly one message", 1, result.size());
    assertEquals("The message ID should match the mock data", "12345", result.get(0).getId());
  }

  @Test
  public void testListMessagesHandlesApiError() {
    // Mock transport simulates a 404 Not Found error
    HttpTransport errorTransport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            return new MockLowLevelHttpRequest() {
              @Override
              public LowLevelHttpResponse execute() {
                MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                response.setStatusCode(404);
                response.setContentType(Json.MEDIA_TYPE);
                response.setContent("{\"error\": {\"code\": 404, \"message\": \"Not Found\"}}");
                return response;
              }
            };
          }
        };

    Gmail service = getMockGmailService(errorTransport);
    assertThrows(GoogleJsonResponseException.class, () -> ListMessages.listMessages(service));
  }

  @Test
  public void testListMessagesHandlesEmptyResults() throws IOException {
    // Mock transport returns empty messages array
    HttpTransport emptyTransport =
        new MockHttpTransport() {
          @Override
          public LowLevelHttpRequest buildRequest(String method, String url) {
            return new MockLowLevelHttpRequest() {
              @Override
              public LowLevelHttpResponse execute() {
                MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                response.setStatusCode(200);
                response.setContentType(Json.MEDIA_TYPE);
                response.setContent("{\"messages\": []}");
                return response;
              }
            };
          }
        };

    Gmail service = getMockGmailService(emptyTransport);
    List<Message> result = ListMessages.listMessages(service);
    assertNotNull("Should return an empty list when no messages found", result);
    assertTrue("Returned list should be empty", result.isEmpty());
  }

  private Gmail getMockGmailService(HttpTransport transport) {
    return new Gmail.Builder(transport, GsonFactory.getDefaultInstance(), request -> {})
        .setApplicationName("Gmail API Snippets")
        .build();
  }
}
