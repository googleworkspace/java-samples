import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.Assert.*;

public class FileSnippetsTest extends BaseTest {

  private FileSnippets snippets;

  @Before
  public void createSnippets() {
    this.snippets = new FileSnippets(this.service);
  }

  @Test
  public void uploadBasic() throws IOException, GeneralSecurityException {
    String id = this.snippets.uploadBasic();
    assertNotNull(id);
    deleteFileOnCleanup(id);
  }

  @Test
  public void uploadRevision() throws IOException, GeneralSecurityException {
    String id = this.snippets.uploadBasic();
    assertNotNull(id);
    deleteFileOnCleanup(id);
    String id2 = this.snippets.uploadRevision(id);
    assertEquals(id, id2);
  }

  @Test
  public void uploadToFolder() throws IOException, GeneralSecurityException {
    String folderId = this.snippets.createFolder();
    File file = this.snippets.uploadToFolder(folderId);
    assertTrue(file.getParents().contains(folderId));
    deleteFileOnCleanup(file.getId());
    deleteFileOnCleanup(folderId);
  }

  @Test
  public void uploadWithConversion()
      throws IOException, GeneralSecurityException {
    String id = this.snippets.uploadWithConversion();
    assertNotNull(id);
    deleteFileOnCleanup(id);
  }

  @Test
  public void exportPdf() throws IOException, GeneralSecurityException {
    String id = createTestDocument();
    ByteArrayOutputStream out = this.snippets.exportPdf(id);
    assertEquals("%PDF", out.toString("UTF-8").substring(0, 4));
  }

  @Test
  public void downloadFile() throws IOException, GeneralSecurityException {
    String id = createTestBlob();
    ByteArrayOutputStream out = this.snippets.downloadFile(id);
    byte[] bytes = out.toByteArray();
    assertEquals((byte) 0xFF, bytes[0]);
    assertEquals((byte) 0xD8, bytes[1]);
  }

  @Test
  public void createShortcut() throws IOException, GeneralSecurityException {
    String id = this.snippets.createShortcut();
    assertNotNull(id);
    deleteFileOnCleanup(id);
  }

  @Test
  public void touchFile() throws IOException, GeneralSecurityException {
    String id = this.createTestBlob();
    long now = System.currentTimeMillis();
    long modifiedTime = this.snippets.touchFile(id, now);
    assertEquals(now, modifiedTime);
  }

  @Test
  public void createFolder() throws IOException, GeneralSecurityException {
    String id = this.snippets.createFolder();
    assertNotNull(id);
    deleteFileOnCleanup(id);
  }

  @Test
  public void moveFileToFolder()
      throws IOException, GeneralSecurityException {
    String folderId = this.snippets.createFolder();
    deleteFileOnCleanup(folderId);
    String fileId = this.createTestBlob();
    List<String> parents = this.snippets.moveFileToFolder(fileId, folderId);
    assertTrue(parents.contains(folderId));
    assertEquals(1, parents.size());
  }

  @Test
  public void searchFiles()
      throws IOException, GeneralSecurityException {
    this.createTestBlob();
    List<File> files = this.snippets.searchFiles();
    assertNotEquals(0, files.size());
  }

  @Test
  public void shareFile() throws IOException {
    String fileId = this.createTestBlob();
    List<String> ids = this.snippets.shareFile(fileId,
        "user@test.appsdevtesting.com",
        "test.appsdevtesting.com");
    assertEquals(2, ids.size());
  }
}
