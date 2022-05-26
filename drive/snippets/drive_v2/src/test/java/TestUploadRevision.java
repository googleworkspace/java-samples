import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestUploadRevision extends BaseTest{
    @Test
    public void uploadRevision() throws IOException, GeneralSecurityException {
        String id = UploadBasic.uploadBasic();
        assertNotNull(id);
        deleteFileOnCleanup(id);
        String id2 = UploadRevision.uploadRevision(id);
        assertEquals(id, id2);
    }
}
