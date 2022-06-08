import com.google.api.services.drive.model.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class TestRecoverDrives extends BaseTest{
    @Test
    public void recoverDrives() throws IOException {
        String id = this.createOrphanedDrive();
        List<Drive> results = RecoverDrive.recoverDrives(
                "sbazyl@test.appsdevtesting.com");
        assertNotEquals(0, results.size());
        this.service.drives().delete(id).execute();
    }

    private String createOrphanedDrive() throws IOException {
        String driveId = CreateDrive.createDrive();
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
