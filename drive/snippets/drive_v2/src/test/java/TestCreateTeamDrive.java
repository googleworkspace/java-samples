import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestCreateTeamDrive extends BaseTest{
    @Test
    public void createTeamDrive() throws IOException, GeneralSecurityException {
        String id = CreateTeamDrive.createTeamDrive();
        assertNotNull(id);
        this.service.teamdrives().delete(id);
    }
}
