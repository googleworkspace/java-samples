import com.google.api.services.gmail.model.*;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SendEmailTest extends BaseTest {

    @Test
    public void createEmail() throws MessagingException, IOException {
        MimeMessage mimeMessage = SendEmail.createEmail(RECIPIENT,
                TEST_USER,
                "test",
                "Hello!");
        assertEquals("test", mimeMessage.getSubject());
        assertEquals("Hello!", mimeMessage.getContent());
        assertEquals(RECIPIENT, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(TEST_USER, mimeMessage.getFrom()[0].toString());
    }

    @Test
    public void createEmailWithAttachment() throws MessagingException, IOException {
        MimeMessage mimeMessage = SendEmail.createEmailWithAttachment(RECIPIENT,
                TEST_USER,
                "test",
                "Hello!",
                new java.io.File("files/photo.jpg"));
        assertEquals("test", mimeMessage.getSubject());
        assertEquals(RECIPIENT, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(TEST_USER, mimeMessage.getFrom()[0].toString());
    }

    @Test
    public void createMessageWithEmail() throws MessagingException, IOException {
        MimeMessage mimeMessage = SendEmail.createEmail(RECIPIENT,
                TEST_USER,
                "test",
                "Hello!");

        com.google.api.services.gmail.model.Message message = SendEmail.createMessageWithEmail(mimeMessage);
        assertNotNull(message.getRaw()); // Weak assertion...
    }

    @Test
    public void createDraft() throws MessagingException, IOException {
        MimeMessage mimeMessage = SendEmail.createEmail(RECIPIENT,
                TEST_USER,
                "test",
                "Hello!");
        Draft draft = SendEmail.createDraft(this.service, "me", mimeMessage);
        assertNotNull(draft);
        this.service.users().drafts().delete("me", draft.getId());
    }

    @Test
    public void sendEmail() throws MessagingException, IOException {
        MimeMessage mimeMessage = SendEmail.createEmailWithAttachment(RECIPIENT,
                TEST_USER,
                "test",
                "Hello!",
                new java.io.File("files/photo.jpg"));
        com.google.api.services.gmail.model.Message message = SendEmail.sendMessage(this.service, "me", mimeMessage);
        assertNotNull(message);
    }
}
