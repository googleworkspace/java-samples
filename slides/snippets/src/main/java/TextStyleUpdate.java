// Copyright 2022 Google LLC
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


// [START slides_text_style_update]
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.Dimension;
import com.google.api.services.slides.v1.model.Link;
import com.google.api.services.slides.v1.model.OpaqueColor;
import com.google.api.services.slides.v1.model.OptionalColor;
import com.google.api.services.slides.v1.model.Range;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.RgbColor;
import com.google.api.services.slides.v1.model.TextStyle;
import com.google.api.services.slides.v1.model.UpdateTextStyleRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Slide Text Structure and Styling API */
public class TextStyleUpdate {
    /**
     * Styles text in the shape.
     *
     * @param presentationId - id of the presentation.
     * @param shapeId - id of the shape.
     * @return shape id
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdatePresentationResponse textStyleUpdate(String presentationId, String shapeId)
            throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singleton(SlidesScopes.PRESENTATIONS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the slides API client
        Slides service = new Slides.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        // Update the text style so that the first 5 characters are bolded
        // and italicized, and the next 5 are displayed in blue 14 pt Times
        // New Roman font, and the next five are hyperlinked.
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("FIXED_RANGE")
                                .setStartIndex(0)
                                .setEndIndex(5))
                        .setStyle(new TextStyle()
                                .setBold(true)
                                .setItalic(true))
                        .setFields("bold,italic")));
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("FIXED_RANGE")
                                .setStartIndex(5)
                                .setEndIndex(10))
                        .setStyle(new TextStyle()
                                .setFontFamily("Times New Roman")
                                .setFontSize(new Dimension()
                                        .setMagnitude(14.0)
                                        .setUnit("PT"))
                                .setForegroundColor(new OptionalColor()
                                        .setOpaqueColor(new OpaqueColor()
                                                .setRgbColor(new RgbColor()
                                                        .setBlue(1.0F)
                                                        .setGreen(0.0F)
                                                        .setRed(0.0F)))))
                        .setFields("foregroundColor,fontFamily,fontSize")));
        requests.add(new Request()
                .setUpdateTextStyle(new UpdateTextStyleRequest()
                        .setObjectId(shapeId)
                        .setTextRange(new Range()
                                .setType("FIXED_RANGE")
                                .setStartIndex(10)
                                .setEndIndex(15))
                        .setStyle(new TextStyle()
                                .setLink(new Link()
                                        .setUrl("www.example.com")))
                        .setFields("link")));

        BatchUpdatePresentationResponse response = null;
        try {
            // Execute the requests.
            BatchUpdatePresentationRequest body =
                    new BatchUpdatePresentationRequest().setRequests(requests);
            response = service.presentations().batchUpdate(presentationId, body).execute();
            System.out.println("Updated text style for shape with ID: " + shapeId);
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 400) {
                System.out.printf("Shape not found with id '%s'.\n", shapeId);
            } else if (error.getCode() == 404) {
                System.out.printf("Presentation not found with id '%s'.\n", presentationId);
            }
            else {
                throw e;
            }
        }
        return response;
    }
}
// [END slides_text_style_update]