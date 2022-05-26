import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class TestFetchStartPageToken extends BaseTest{
    @Test
    public void fetchStartPageToken() throws IOException {
        String token = FetchStartPageToken.fetchStartPageToken();
        assertNotNull(token);
    }
}
