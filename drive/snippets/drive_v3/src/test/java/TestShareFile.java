import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestShareFile extends BaseTest{
    @Test
    public void shareFile() throws IOException {
        String fileId = this.createTestBlob();
        List<String> ids = ShareFile.shareFile(fileId,
                "user@test.appsdevtesting.com",
                "test.appsdevtesting.com");
        assertEquals(2, ids.size());
    }
}
