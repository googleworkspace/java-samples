import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;

public class TestTouchFile extends BaseTest{
    @Test
    public void touchFile() throws IOException, GeneralSecurityException {
        String id = this.createTestBlob();
        long now = System.currentTimeMillis();
        long modifiedTime = TouchFile.touchFile(id, now);
        assertEquals(now, modifiedTime);
    }
}
