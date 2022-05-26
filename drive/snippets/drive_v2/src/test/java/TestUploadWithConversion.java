import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestUploadWithConversion extends BaseTest{
    @Test
    public void uploadWithConversion()
            throws IOException, GeneralSecurityException {
        String id = UploadWithConversion.uploadWithConversion();
        assertNotNull(id);
        deleteFileOnCleanup(id);
    }
}
