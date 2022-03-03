import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.*;

public class AppDataSnippetsTest extends BaseTest {

  private AppDataSnippets snippets;

  @Before
  public void createSnippets() {
    this.snippets = new AppDataSnippets(this.service);
  }

  @Test
  public void fetchAppDataFolder() throws IOException, GeneralSecurityException {
    String id = this.snippets.fetchAppDataFolder();
    assertNotNull(id);
  }

  @Test
  public void uploadAppData()
      throws IOException, GeneralSecurityException {
    String id = this.snippets.uploadAppData();
    assertNotNull(id);
    deleteFileOnCleanup(id);
  }

  @Test
  public void listAppData() throws IOException, GeneralSecurityException {
    String id = this.snippets.uploadAppData();
    deleteFileOnCleanup(id);
    FileList files = this.snippets.listAppData();
    assertNotEquals(0, files.getItems().size());
  }

}
