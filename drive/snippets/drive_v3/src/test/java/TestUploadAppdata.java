import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestUploadAppdata extends BaseTest{
    @Test
    public void uploadAppData()
            throws IOException, GeneralSecurityException {
        String id = UploadAppData.uploadAppData();
        assertNotNull(id);
        deleteFileOnCleanup(id);
    }
}
