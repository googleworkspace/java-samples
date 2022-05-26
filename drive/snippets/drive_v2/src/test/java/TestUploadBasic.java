import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;

public class TestUploadBasic {

    @Test
    public void uploadBasic() throws IOException, GeneralSecurityException {
        String id = UploadBasic.uploadBasic();
        assertNotNull(id);
    }
}
