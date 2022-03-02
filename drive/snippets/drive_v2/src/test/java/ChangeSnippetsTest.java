import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ChangeSnippetsTest extends BaseTest {

  private ChangeSnippets snippets;

  @Before
  public void createSnippets() {
    this.snippets = new ChangeSnippets(this.service);
  }

  @Test
  public void fetchStartPageToken() throws IOException {
    String token = this.snippets.fetchStartPageToken();
    assertNotNull(token);
  }

  @Test
  public void fetchChanges() throws IOException {
    String startPageToken = this.snippets.fetchStartPageToken();
    this.createTestBlob();
    String newStartPageToken = this.snippets.fetchChanges(startPageToken);
    assertNotNull(newStartPageToken);
    assertNotEquals(startPageToken, newStartPageToken);
  }
}
