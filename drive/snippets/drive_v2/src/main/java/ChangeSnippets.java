import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.StartPageToken;

import java.io.IOException;

public class ChangeSnippets {

  private Drive service;

  public ChangeSnippets(Drive service) {
    this.service = service;
  }

  public String fetchStartPageToken() throws IOException {
    Drive driveService = this.service;
    // [START fetchStartPageToken]
    StartPageToken response = driveService.changes()
        .getStartPageToken().execute();
    System.out.println("Start token: " + response.getStartPageToken());
    // [END fetchStartPageToken]
    return response.getStartPageToken();
  }

  public String fetchChanges(String savedStartPageToken) throws IOException {
    Drive driveService = this.service;
    // [START fetchChanges]
    // Begin with our last saved start token for this user or the
    // current token from getStartPageToken()
    String pageToken = savedStartPageToken;
    while (pageToken != null) {
      ChangeList changes = driveService.changes().list()
          .setPageToken(pageToken)
          .execute();
      for (Change change : changes.getItems()) {
        // Process change
        System.out.println("Change found for file: " + change.getFileId());
      }
      if (changes.getNewStartPageToken() != null) {
        // Last page, save this token for the next polling interval
        savedStartPageToken = changes.getNewStartPageToken();
      }
      pageToken = changes.getNextPageToken();
    }
    // [END fetchChanges]
    return savedStartPageToken;
  }
}
