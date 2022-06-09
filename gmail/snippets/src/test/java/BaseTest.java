import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.Before;
import java.io.IOException;

public class BaseTest {

    public static final String TEST_USER = "ci-test01@workspacesamples.dev";
    public static final String RECIPIENT = "gduser1@workspacesamples.dev";
    public static final String FORWARDING_ADDRESS = "gduser1@workspacesamples.dev";

    protected Gmail service;

    /**
     * Create a default authorization Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException - if credentials file not found.
     */
    public Gmail buildService() throws IOException {
        /* Load pre-authorized user credentials from the environment.
         TODO(developer) - See https://developers.google.com/identity for
          guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(GmailScopes.GMAIL_SETTINGS_BASIC,
                GmailScopes.GMAIL_COMPOSE,
                GmailScopes.GMAIL_SETTINGS_SHARING,
                GmailScopes.GMAIL_LABELS);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Create the Gmail API client
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName("Gmail API Snippets")
                    .build();
        return service;
    }

    @Before
    public void setupService() throws IOException {
        this.service = buildService();
    }
}
