import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestCreateDrive extends BaseTest{
    @Test
    public void createDrive() throws IOException, GeneralSecurityException {
        String id = CreateDrive.createDrive();
        assertNotNull(id);
        this.service.drives().delete(id);
    }
}
