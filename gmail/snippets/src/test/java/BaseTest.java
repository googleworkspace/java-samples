import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.junit.Before;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BaseTest {

    public static final String TEST_USER = "gdtest1@appsrocks.com";
    public static final String RECIPIENT = "gdtest2@appsrocks.com";
    public static final String FORWARDING_ADDRESS = "gdtest2@appsrocks.com";

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

    public GoogleCredential getCredential() throws IOException {
        GoogleCredential defaultCredentials = GoogleCredential.getApplicationDefault();
        return new GoogleCredential.Builder()
                .setServiceAccountId(defaultCredentials.getServiceAccountId())
                .setServiceAccountPrivateKey(defaultCredentials.getServiceAccountPrivateKey())
                .setServiceAccountPrivateKeyId(defaultCredentials.getServiceAccountPrivateKeyId())
                .setServiceAccountUser(TEST_USER)
                .setJsonFactory(defaultCredentials.getJsonFactory())
                .setTransport(defaultCredentials.getTransport())
                .setServiceAccountScopes(Arrays.asList(GmailScopes.GMAIL_COMPOSE, GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_SETTINGS_BASIC, GmailScopes.GMAIL_SETTINGS_SHARING))
                .build();
    }

    public Gmail buildService() throws IOException, GeneralSecurityException {
        GoogleCredential credential = getCredential();
        return new Gmail.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Drive API Snippets")
                .build();
    }

    @Before
    public void setup() throws IOException, GeneralSecurityException {
        this.service = buildService();
    }

}
