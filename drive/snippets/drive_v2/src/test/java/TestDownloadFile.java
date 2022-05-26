import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;

public class TestDownloadFile extends BaseTest{
    @Test
    public void downloadFile() throws IOException, GeneralSecurityException {
        String id = createTestBlob();
        ByteArrayOutputStream out = DownloadFile.downloadFile(id);
        byte[] bytes = out.toByteArray();
        assertEquals((byte) 0xFF, bytes[0]);
        assertEquals((byte) 0xD8, bytes[1]);
    }
}
