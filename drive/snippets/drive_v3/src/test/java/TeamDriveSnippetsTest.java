import com.google.api.services.drive.model.TeamDrive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class TeamDriveSnippetsTest extends BaseTest {

  private TeamDriveSnippets snippets;

  @Before
  public void createSnippets() {
    this.snippets = new TeamDriveSnippets(this.service);
  }

  @Test
  public void createTeamDrive() throws IOException, GeneralSecurityException {
    String id = this.snippets.createTeamDrive();
    assertNotNull(id);
    this.service.teamdrives().delete(id);
  }

  @Test
  public void recoverTeamDrives() throws IOException {
    String id = this.createOrphanedTeamDrive();
    List<TeamDrive> results = this.snippets.recoverTeamDrives(
        "sbazyl@test.appsdevtesting.com");
    assertNotEquals(0, results.size());
    this.service.teamdrives().delete(id).execute();
  }

  private String createOrphanedTeamDrive() throws IOException {
    String teamDriveId = this.snippets.createTeamDrive();
    PermissionList response = this.service.permissions().list(teamDriveId)
        .setSupportsTeamDrives(true)
        .execute();
    for (Permission permission : response.getPermissions()) {
      this.service.permissions().delete(teamDriveId, permission.getId())
          .setSupportsTeamDrives(true)
          .execute();
    }
    return teamDriveId;
  }
}
