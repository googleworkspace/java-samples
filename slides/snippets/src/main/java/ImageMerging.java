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


// [START slides_image_merging]
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.SlidesScopes;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationResponse;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.Response;
import com.google.api.services.slides.v1.model.ReplaceAllShapesWithImageRequest;
import com.google.api.services.slides.v1.model.SubstringMatchCriteria;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate the use of Slides Image Merging API */
public class ImageMerging {
    /**
     * Changes specified texts into images.
     *
     * @param templatePresentationId - id of the presentation.
     * @param imageUrl - Url of the image.
     * @param customerName - Name of the customer.
     * @return merged presentation id
     * @throws IOException - if credentials file not found.
     */
    public static BatchUpdatePresentationResponse imageMerging(String templatePresentationId,
                                                               String imageUrl,
                                                               String customerName) throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Arrays.asList(SlidesScopes.PRESENTATIONS,
                        SlidesScopes.DRIVE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Create the slides API client
        Slides service = new Slides.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        // Create the drive API client
        Drive driveService = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Slides samples")
                .build();

        // Duplicate the template presentation using the Drive API.
        String copyTitle = customerName + " presentation";
        File content = new File().setName(copyTitle);
        File presentationFile =
                driveService.files().copy(templatePresentationId, content).execute();
        String presentationId = presentationFile.getId();

        // Create the image merge (replaceAllShapesWithImage) requests.
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setReplaceAllShapesWithImage(new ReplaceAllShapesWithImageRequest()
                        .setImageUrl(imageUrl)
                        .setImageReplaceMethod("CENTER_INSIDE")
                        .setContainsText(new SubstringMatchCriteria()
                                .setText("{{company-logo}}")
                                .setMatchCase(true))));

        // Execute the requests.
        BatchUpdatePresentationRequest body =
                new BatchUpdatePresentationRequest().setRequests(requests);
        BatchUpdatePresentationResponse response =
                service.presentations().batchUpdate(presentationId, body).execute();

        int numReplacements = 0;
        try {
            // Count total number of replacements made.
            for (Response resp : response.getReplies()) {
                numReplacements += resp.getReplaceAllShapesWithImage().getOccurrencesChanged();
            }

            // Prints the merged presentation id and count of replacements.
            System.out.println("Created merged presentation with ID: " + presentationId);
            System.out.println("Replaced " + numReplacements + " shapes instances with images.");
        } catch (NullPointerException ne) {
            System.out.println("Text not found to replace with image.");
        }
        return response;
    }
}
// [END slides_image_merging]