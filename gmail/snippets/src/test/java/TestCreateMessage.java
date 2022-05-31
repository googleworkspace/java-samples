import org.junit.Test;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import static org.junit.Assert.assertNotNull;

// Unit testcase for gmail create message snippet
public class TestCreateMessage extends BaseTest{

    @Test
    public void testCreateMessageWithEmail() throws MessagingException,
            IOException {
        MimeMessage mimeMessage = CreateEmail.createEmail(RECIPIENT,
                TEST_USER,
                "test",
                "Hello!");

        com.google.api.services.gmail.model.Message message = CreateMessage.createMessageWithEmail(mimeMessage);
        assertNotNull(message.getRaw()); // Weak assertion...
    }
}
