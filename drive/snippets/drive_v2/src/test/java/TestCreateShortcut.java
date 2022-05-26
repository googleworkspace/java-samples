import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestCreateShortcut extends BaseTest{
    @Test
    public void createShortcut() throws IOException, GeneralSecurityException {
        String id = CreateShortcut.createShortcut();
        assertNotNull(id);
        deleteFileOnCleanup(id);
    }
}
