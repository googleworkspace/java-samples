import com.google.api.services.drive.model.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class DriveSnippetsTest extends BaseTest {

  private DriveSnippets snippets;

  @Before
  public void createSnippets() {
    this.snippets = new DriveSnippets(this.service);
  }

  @Test
  public void createDrive() throws IOException, GeneralSecurityException {
    String id = this.snippets.createDrive();
    assertNotNull(id);
    this.service.drives().delete(id);
  }

  @Test
  public void recoverDrives() throws IOException {
    String id = this.createOrphanedDrive();
    List<Drive> results = this.snippets.recoverDrives(
        "sbazyl@test.appsdevtesting.com");
    assertNotEquals(0, results.size());
    this.service.drives().delete(id).execute();
  }

  private String createOrphanedDrive() throws IOException {
    String driveId = this.snippets.createDrive();
    PermissionList response = this.service.permissions().list(driveId)
        .setSupportsAllDrives(true)
        .execute();
    for (Permission permission : response.getItems()) {
      this.service.permissions().delete(driveId, permission.getId())
          .setSupportsAllDrives(true)
          .execute();
    }
    return driveId;
  }
}
