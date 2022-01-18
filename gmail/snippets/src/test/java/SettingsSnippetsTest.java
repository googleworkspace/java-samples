
import com.google.api.services.gmail.model.AutoForwarding;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.VacationSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.*;

public class SettingsSnippetsTest extends BaseTest {

    private SettingsSnippets snippets;
    private Label testLabel = null;

    @Before
    public void createSnippets() {
        this.snippets = new SettingsSnippets(this.service);
    }

    @Before
    public void createLabel() throws IOException {
        ListLabelsResponse response = this.service.users().labels().list("me").execute();
        for (Label l : response.getLabels()) {
            if (l.getName().equals("testLabel")) {
                testLabel = l;
            }
        }
        if (testLabel == null) {
            Label label = new Label()
                    .setName("testLabel")
                    .setLabelListVisibility("labelShow")
                    .setMessageListVisibility("show");
            testLabel = this.service.users().labels().create("me", label).execute();
        }
    }

    @After
    public void deleteLabel() throws IOException {
        if (testLabel != null) {
            this.service.users().labels().delete("me", testLabel.getId()).execute();
            testLabel = null;
        }
    }

    @Test
    public void updateSignature() throws IOException, GeneralSecurityException {
        String signature = this.snippets.updateSignature();
        assertEquals("I heart cats.", signature);
    }

    @Test
    public void createFilter() throws IOException, GeneralSecurityException {
        String id = this.snippets.createFilter(testLabel.getId());
        assertNotNull(id);
        this.service.users().settings().filters().delete("me", id).execute();
    }

    @Test
    public void enableAutoForwarding() throws IOException, GeneralSecurityException {
        AutoForwarding forwarding = this.snippets.enableForwarding(FORWARDING_ADDRESS);
        assertNotNull(forwarding);
        forwarding = new AutoForwarding().setEnabled(false);
        this.service.users().settings().updateAutoForwarding("me", forwarding).execute();
        this.service.users().settings().forwardingAddresses().delete("me", FORWARDING_ADDRESS).execute();
    }

    @Test
    public void enableAutoReply() throws IOException, GeneralSecurityException {
        VacationSettings settings = this.snippets.enableAutoReply();
        assertNotNull(settings);
    }
}
