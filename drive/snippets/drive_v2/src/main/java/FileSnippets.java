import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.ParentReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileSnippets {

  private Drive service;

  public FileSnippets(Drive service) {
    this.service = service;
  }

  public String uploadBasic() throws IOException {
    Drive driveService = this.service;
    // [START uploadBasic]
    File fileMetadata = new File();
    fileMetadata.setTitle("photo.jpg");
    java.io.File filePath = new java.io.File("files/photo.jpg");
    FileContent mediaContent = new FileContent("image/jpeg", filePath);
    File file = driveService.files().insert(fileMetadata, mediaContent)
        .setFields("id")
        .execute();
    System.out.println("File ID: " + file.getId());
    // [END uploadBasic]
    return file.getId();
  }

  public String uploadRevision(String realFileId) throws IOException {
    Drive driveService = this.service;
    // [START uploadRevision]
    String fileId = "0BwwA4oUTeiV1UVNwOHItT0xfa2M";
    // [START_EXCLUDE silent]
    fileId = realFileId;
    // [END_EXCLUDE]
    java.io.File filePath = new java.io.File("files/photo.jpg");
    FileContent mediaContent = new FileContent("image/jpeg", filePath);
    Drive.Files.Update request = driveService.files().update(fileId, new File(), mediaContent)
        .setFields("id");
    request.getMediaHttpUploader().setDirectUploadEnabled(true);
    File file = request.execute();
    System.out.println("File ID: " + file.getId());
    // [END uploadRevision]
    return file.getId();
  }


  public File uploadToFolder(String realFolderId) throws IOException {
    Drive driveService = this.service;
    // [START uploadToFolder]
    String folderId = "0BwwA4oUTeiV1TGRPeTVjaWRDY1E";
    File fileMetadata = new File();
    // [START_EXCLUDE silent]
    folderId = realFolderId;
    // [END_EXCLUDE]
    fileMetadata.setTitle("photo.jpg");
    fileMetadata.setParents(Collections.singletonList(
        new ParentReference().setId(folderId)));
    java.io.File filePath = new java.io.File("files/photo.jpg");
    FileContent mediaContent = new FileContent("image/jpeg", filePath);
    File file = driveService.files().insert(fileMetadata, mediaContent)
        .setFields("id, parents")
        .execute();
    System.out.println("File ID: " + file.getId());
    // [END uploadToFolder]
    return file;
  }

  public String uploadWithConversion() throws IOException {
    Drive driveService = this.service;

    // [START uploadWithConversion]
    File fileMetadata = new File();
    fileMetadata.setTitle("My Report");
    fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");

    java.io.File filePath = new java.io.File("files/report.csv");
    FileContent mediaContent = new FileContent("text/csv", filePath);
    File file = driveService.files().insert(fileMetadata, mediaContent)
        .setFields("id")
        .execute();
    System.out.println("File ID: " + file.getId());
    // [END uploadWithConversion]
    return file.getId();
  }

  public ByteArrayOutputStream exportPdf(String realFileId)
      throws IOException {
    Drive driveService = this.service;
    // [START exportPdf]
    String fileId = "1ZdR3L3qP4Bkq8noWLJHSr_iBau0DNT4Kli4SxNc2YEo";
    OutputStream outputStream = new ByteArrayOutputStream();
    // [START_EXCLUDE silent]
    fileId = realFileId;
    // [END_EXCLUDE]
    driveService.files().export(fileId, "application/pdf")
        .executeMediaAndDownloadTo(outputStream);
    // [END exportPdf]
    return (ByteArrayOutputStream) outputStream;
  }

  public ByteArrayOutputStream downloadFile(String realFileId)
      throws IOException {
    Drive driveService = this.service;
    // [START downloadFile]
    String fileId = "0BwwA4oUTeiV1UVNwOHItT0xfa2M";
    OutputStream outputStream = new ByteArrayOutputStream();
    // [START_EXCLUDE silent]
    fileId = realFileId;
    // [END_EXCLUDE]
    driveService.files().get(fileId)
        .executeMediaAndDownloadTo(outputStream);
    // [END downloadFile]
    return (ByteArrayOutputStream) outputStream;
  }

  public String createShortcut() throws IOException {
    Drive driveService = this.service;
    // [START createShortcut]
    File fileMetadata = new File();
    fileMetadata.setTitle("Project plan");
    fileMetadata.setMimeType("application/vnd.google-apps.drive-sdk");

    File file = driveService.files().insert(fileMetadata)
        .setFields("id")
        .execute();
    System.out.println("File ID: " + file.getId());
    // [END createShortcut]
    return file.getId();
  }

  public long touchFile(String realFileId, long realTimestamp)
      throws IOException {
    Drive driveService = this.service;

    // [START touchFile]
    String fileId = "1sTWaJ_j7PkjzaBWtNc3IzovK5hQf21FbOw9yLeeLPNQ";
    File fileMetadata = new File();
    fileMetadata.setModifiedDate(new DateTime(System.currentTimeMillis()));
    // [START_EXCLUDE silent]
    fileId = realFileId;
    fileMetadata.setModifiedDate(new DateTime(realTimestamp));
    // [END_EXCLUDE]
    File file = driveService.files().update(fileId, fileMetadata)
        .setSetModifiedDate(true)
        .setFields("id, modifiedDate")
        .execute();
    System.out.println("Modified time: " + file.getModifiedDate());
    // [END touchFile]
    return file.getModifiedDate().getValue();
  }

  public String createFolder() throws IOException {
    Drive driveService = this.service;
    // [START createFolder]
    File fileMetadata = new File();
    fileMetadata.setTitle("Invoices");
    fileMetadata.setMimeType("application/vnd.google-apps.folder");

    File file = driveService.files().insert(fileMetadata)
        .setFields("id")
        .execute();
    System.out.println("Folder ID: " + file.getId());
    // [END createFolder]
    return file.getId();
  }

  public List<String> moveFileToFolder(String realFileId, String realFolderId)
      throws IOException {
    Drive driveService = this.service;
    // [START moveFileToFolder]
    String fileId = "1sTWaJ_j7PkjzaBWtNc3IzovK5hQf21FbOw9yLeeLPNQ";
    String folderId = "0BwwA4oUTeiV1TGRPeTVjaWRDY1E";
    // [START_EXCLUDE silent]
    fileId = realFileId;
    folderId = realFolderId;
    // [END_EXCLUDE]
    // Retrieve the existing parents to remove
    File file = driveService.files().get(fileId)
        .setFields("parents")
        .execute();
    StringBuilder previousParents = new StringBuilder();
    for (ParentReference parent : file.getParents()) {
      previousParents.append(parent.getId());
      previousParents.append(',');
    }
    // Move the file to the new folder
    file = driveService.files().update(fileId, null)
        .setAddParents(folderId)
        .setRemoveParents(previousParents.toString())
        .setFields("id, parents")
        .execute();
    // [END moveFileToFolder]
    List<String> parents = new ArrayList<String>();
    for (ParentReference parent : file.getParents()) {
      parents.add(parent.getId());
    }
    return parents;
  }

  public List<File> searchFiles() throws IOException {
    Drive driveService = this.service;
    List<File> files = new ArrayList<File>();
    // [START searchFiles]
    String pageToken = null;
    do {
      FileList result = driveService.files().list()
          .setQ("mimeType='image/jpeg'")
          .setSpaces("drive")
          .setFields("nextPageToken, items(id, title)")
          .setPageToken(pageToken)
          .execute();
      for (File file : result.getItems()) {
        System.out.printf("Found file: %s (%s)\n",
            file.getTitle(), file.getId());
      }
      // [START_EXCLUDE silent]
      files.addAll(result.getItems());
      // [END_EXCLUDE]
      pageToken = result.getNextPageToken();
    } while (pageToken != null);
    // [END searchFiles]
    return files;
  }

  public List<String> shareFile(String realFileId, String realUser, String realDomain)
      throws IOException {
    Drive driveService = this.service;
    final List<String> ids = new ArrayList<String>();
    // [START shareFile]
    String fileId = "1sTWaJ_j7PkjzaBWtNc3IzovK5hQf21FbOw9yLeeLPNQ";
    // [START_EXCLUDE silent]
    fileId = realFileId;
    // [END_EXCLUDE]
    JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
      @Override
      public void onFailure(GoogleJsonError e,
                            HttpHeaders responseHeaders)
          throws IOException {
        // Handle error
        System.err.println(e.getMessage());
      }

      @Override
      public void onSuccess(Permission permission,
                            HttpHeaders responseHeaders)
          throws IOException {
        System.out.println("Permission ID: " + permission.getId());
        // [START_EXCLUDE silent]
        ids.add(permission.getId());
        // [END_EXCLUDE]
      }
    };
    BatchRequest batch = driveService.batch();
    Permission userPermission = new Permission()
        .setType("user")
        .setRole("writer")
        .setValue("user@example.com");
    // [START_EXCLUDE silent]
    userPermission.setValue(realUser);
    // [END_EXCLUDE]
    driveService.permissions().insert(fileId, userPermission)
        .setFields("id")
        .queue(batch, callback);

    Permission domainPermission = new Permission()
        .setType("domain")
        .setRole("reader")
        .setValue("example.com");
    // [START_EXCLUDE silent]
    domainPermission.setValue(realDomain);
    // [END_EXCLUDE]
    driveService.permissions().insert(fileId, domainPermission)
        .setFields("id")
        .queue(batch, callback);

    batch.execute();
    // [END shareFile]
    return ids;
  }

}
