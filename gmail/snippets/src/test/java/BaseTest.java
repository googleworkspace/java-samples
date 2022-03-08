import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BaseTest {


    public static final String TEST_USER = "ci-test01@workspacesamples.dev";
    public static final String RECIPIENT = "gduser01@workspacesamples.dev";
    public static final String FORWARDING_ADDRESS = "gduser01@workspacesamples.dev";

    static {
        enableLogging();
    }

    protected Gmail service;


    public static void enableLogging() {
        Logger logger = Logger.getLogger(HttpTransport.class.getName());
        logger.setLevel(Level.ALL);
        logger.addHandler(new Handler() {

            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(LogRecord record) {
                // default ConsoleHandler will print >= INFO to System.err
                if (record.getLevel().intValue() < Level.INFO.intValue()) {
                    System.out.println(record.getMessage());
                }
            }
        });
    }

    public Gmail buildService() throws IOException {
        String serviceAccountPath = System.getenv("SERVICE_ACCOUNT_CREDENTIALS");
        try (InputStream stream = new FileInputStream(serviceAccountPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            credentials = credentials.createScoped(
                    "email",
                    GmailScopes.MAIL_GOOGLE_COM,
                    GmailScopes.GMAIL_SETTINGS_BASIC,
                    GmailScopes.GMAIL_COMPOSE,
                    GmailScopes.GMAIL_SETTINGS_SHARING,
                    GmailScopes.GMAIL_LABELS).createDelegated(TEST_USER);
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
            return new Gmail.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName("Gmail API Snippets")
                    .build();
        }
    }

    @Before
    public void setupService() throws IOException {
        this.service = buildService();
    }

    public MockedStatic<GoogleCredentials> useServiceAccount() throws IOException {
        MockedStatic<GoogleCredentials> mockedGoogleCredentials;
        String serviceAccountPath = System.getenv("SERVICE_ACCOUNT_CREDENTIALS");
        try (InputStream stream = new FileInputStream(serviceAccountPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            mockedGoogleCredentials = Mockito.mockStatic(GoogleCredentials.class);
            mockedGoogleCredentials.when(GoogleCredentials::getApplicationDefault).thenReturn(credentials);
            return mockedGoogleCredentials;
        }
    }

}
