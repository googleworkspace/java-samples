import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class TestFetchChanges extends BaseTest{
    @Test
    public void fetchChanges() throws IOException {
        String startPageToken = FetchStartPageToken.fetchStartPageToken();
        this.createTestBlob();
        String newStartPageToken = FetchChanges.fetchChanges(startPageToken);
        assertNotNull(newStartPageToken);
        assertNotEquals(startPageToken, newStartPageToken);
    }
}
