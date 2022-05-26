import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestFetchAppDataFolder {
    @Test
    public void fetchAppDataFolder() throws IOException, GeneralSecurityException {
        String id = FetchAppDataFolder.fetchAppDataFolder();
        assertNotNull(id);
    }
}
