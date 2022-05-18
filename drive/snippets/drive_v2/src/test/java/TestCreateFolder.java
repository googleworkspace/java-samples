import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestCreateFolder {
    @Test
    public void createFolder() throws IOException, GeneralSecurityException {
        String id = CreateFolder.createFolder();
        assertNotNull(id);
    }
}
