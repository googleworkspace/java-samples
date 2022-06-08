import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.drive.model.TeamDrive;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class TestRecoverTeamDrives extends BaseTest{
    @Test
    public void recoverTeamDrives() throws IOException {
        String id = this.createOrphanedTeamDrive();
        List<TeamDrive> results = RecoverTeamDrive.recoverTeamDrives(
                "sbazyl@test.appsdevtesting.com");
        assertNotEquals(0, results.size());
        this.service.teamdrives().delete(id).execute();
    }
    private String createOrphanedTeamDrive() throws IOException {
        String teamDriveId = CreateTeamDrive.createTeamDrive();
        PermissionList response = this.service.permissions().list(teamDriveId)
                .setSupportsTeamDrives(true)
                .execute();
        for (Permission permission : response.getItems()) {
            this.service.permissions().delete(teamDriveId, permission.getId())
                    .setSupportsTeamDrives(true)
                    .execute();
        }
        return teamDriveId;
    }
}
