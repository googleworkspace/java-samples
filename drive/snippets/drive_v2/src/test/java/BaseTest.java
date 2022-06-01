import com.google.api.client.http.HttpRequestInitializer;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BaseTest {
  static {
    enableLogging();
  }

  protected Drive service;
  protected Set<String> filesToDelete = new HashSet<String>();


  public static void enableLogging() {
    Logger logger = Logger.getLogger(HttpTransport.class.getName());
    logger.setLevel(Level.ALL);
    logger.addHandler(new Handler() {

      @Override
      public void close() throws SecurityException {
      }

      @Override
      public void flush() {
      }

      @Override
      public void publish(LogRecord record) {
        // default ConsoleHandler will print >= INFO to System.err
        if (record.getLevel().intValue() < Level.INFO.intValue()) {
          System.out.println(record.getMessage());
        }
      }
    });
  }

  public GoogleCredentials getCredential() throws IOException {
    return GoogleCredentials.getApplicationDefault()
        .createScoped(Arrays.asList(DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA,DriveScopes.DRIVE_FILE));
  }

  /**
   * Creates a default authorization Drive client service.
   *
   * @return an authorized Drive client service
   * @throws IOException - if credentials file not found.
   */
  protected Drive buildService() throws IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = getCredential();
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
            credentials);

    // Create the classroom API client
    Drive service = new Drive.Builder(new NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer)
            .setApplicationName("Drive Snippets")
            .build();

    return service;
  }

  @Before
  public void setup() throws IOException, GeneralSecurityException {
    this.service = buildService();
    this.filesToDelete.clear();
  }

  @After
  public void cleanupFiles() {
    for (String id : filesToDelete) {
      try {
        this.service.files().delete(id).execute();
      } catch (IOException e) {
        System.err.println("Unable to cleanup file " + id);
      }
    }
  }

  protected void deleteFileOnCleanup(String id) throws IOException {
    filesToDelete.add(id);
  }

  protected String createTestDocument() throws IOException {
    File fileMetadata = new File();
    fileMetadata.setTitle("Test Document");
    fileMetadata.setMimeType("application/vnd.google-apps.document");

    java.io.File filePath = new java.io.File("files/document.txt");
    FileContent mediaContent = new FileContent("text/plain", filePath);
    File file = this.service.files().insert(fileMetadata, mediaContent)
        .setFields("id")
        .execute();
    filesToDelete.add(file.getId());
    return file.getId();
  }

  protected String createTestBlob() throws IOException {
    File fileMetadata = new File();
    fileMetadata.setTitle("photo.jpg");
    java.io.File filePath = new java.io.File("files/photo.jpg");
    FileContent mediaContent = new FileContent("image/jpeg", filePath);
    File file = this.service.files().insert(fileMetadata, mediaContent)
        .setFields("id")
        .execute();
    filesToDelete.add(file.getId());
    return file.getId();
  }
}
