// Copyright 2019 Google LLC
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

// [START drive_activity_v2_quickstart]
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.driveactivity.v2.DriveActivityScopes;
import com.google.api.services.driveactivity.v2.model.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DriveActivityQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME = "Drive Activity API Java Quickstart";

    /** Directory to store authorization tokens for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File("tokens");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * <p>If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/driveactivity-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(DriveActivityScopes.DRIVE_ACTIVITY_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = DriveActivityQuickstart.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
                        .authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive Activity client service.
     *
     * @return an authorized DriveActivity client service
     * @throws IOException
     */
    public static com.google.api.services.driveactivity.v2.DriveActivity getDriveActivityService()
            throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.driveactivity.v2.DriveActivity.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        com.google.api.services.driveactivity.v2.DriveActivity service = getDriveActivityService();

        // Print the recent activity in your Google Drive.
        QueryDriveActivityResponse result =
                service.activity().query(new QueryDriveActivityRequest().setPageSize(10)).execute();
        List<DriveActivity> activities = result.getActivities();
        if (activities == null || activities.size() == 0) {
            System.out.println("No activity.");
        } else {
            System.out.println("Recent activity:");
            for (DriveActivity activity : activities) {
                String time = getTimeInfo(activity);
                String action = getActionInfo(activity.getPrimaryActionDetail());
                List<String> actors =
                        activity.getActors().stream()
                                .map(DriveActivityQuickstart::getActorInfo)
                                .collect(Collectors.toList());
                List<String> targets =
                        activity.getTargets().stream()
                                .map(DriveActivityQuickstart::getTargetInfo)
                                .collect(Collectors.toList());
                System.out.printf(
                        "%s: %s, %s, %s\n", time, truncated(actors), action, truncated(targets));
            }
        }
    }

    /** Returns a string representation of the first elements in a list. */
    private static String truncated(List<String> array) {
        return truncatedTo(array, 2);
    }

    /** Returns a string representation of the first elements in a list. */
    private static String truncatedTo(List<String> array, int limit) {
        String contents = array.stream().limit(limit).collect(Collectors.joining(", "));
        String more = array.size() > limit ? ", ..." : "";
        return "[" + contents + more + "]";
    }

    /** Returns the name of a set property in an object, or else "unknown". */
    private static <T> String getOneOf(AbstractMap<String, T> obj) {
        Iterator<String> iterator = obj.keySet().iterator();
        return iterator.hasNext() ? iterator.next() : "unknown";
    }

    /** Returns a time associated with an activity. */
    private static String getTimeInfo(DriveActivity activity) {
        if (activity.getTimestamp() != null) {
            return activity.getTimestamp();
        }
        if (activity.getTimeRange() != null) {
            return activity.getTimeRange().getEndTime();
        }
        return "unknown";
    }

    /** Returns the type of action. */
    private static String getActionInfo(ActionDetail actionDetail) {
        return getOneOf(actionDetail);
    }

    /** Returns user information, or the type of user if not a known user. */
    private static String getUserInfo(User user) {
        if (user.getKnownUser() != null) {
            KnownUser knownUser = user.getKnownUser();
            Boolean isMe = knownUser.getIsCurrentUser();
            return (isMe != null && isMe) ? "people/me" : knownUser.getPersonName();
        }
        return getOneOf(user);
    }

    /** Returns actor information, or the type of actor if not a user. */
    private static String getActorInfo(Actor actor) {
        if (actor.getUser() != null) {
            return getUserInfo(actor.getUser());
        }
        return getOneOf(actor);
    }

    /** Returns the type of a target and an associated title. */
    private static String getTargetInfo(Target target) {
        if (target.getDriveItem() != null) {
            return "driveItem:\"" + target.getDriveItem().getTitle() + "\"";
        }
        if (target.getTeamDrive() != null) {
            return "teamDrive:\"" + target.getTeamDrive().getTitle() + "\"";
        }
        if (target.getFileComment() != null) {
            DriveItem parent = target.getFileComment().getParent();
            if (parent != null) {
                return "fileComment:\"" + parent.getTitle() + "\"";
            }
            return "fileComment:unknown";
        }
        return getOneOf(target);
    }
}
// [END drive_activity_v2_quickstart]
