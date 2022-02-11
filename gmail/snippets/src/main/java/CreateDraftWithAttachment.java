// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


// [START gmail_create_draft_with_attachment]
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/* Class to demonstrate the use of Gmail Create Draft with attachment API */
public class CreateDraftWithAttachment {
    /**
     * Create a draft email.
     *
     * @param service - Authorized Gmail API service instance.
     * @param fromEmailAddress - Email address to appear in the from: header.
     * @param toEmailAddress - Email address of the recipient.
     * @param file - Path to the file to be attached.
     * @return the created draft
     * @throws MessagingException - if a wrongly formatted address is encountered.
     * @throws IOException - if service account credentials file not found.
     */
    public static Draft createDraftMessageWithAttachment(Gmail service,
                                                         String fromEmailAddress,
                                                         String toEmailAddress,
                                                         File file)
            throws MessagingException, IOException {
        // Create the email content
        String messageSubject = "Automated test mail";
        String bodyText = "Hello. This is an automated test mail.";

        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(toEmailAddress));
        email.setSubject(messageSubject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);
        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());
        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        try {
            // Create the draft message
            Draft draft = new Draft();
            draft.setMessage(message);
            draft = service.users().drafts().create("me", draft).execute();
            System.out.println("Draft id: " + draft.getId());
            System.out.println(draft.toPrettyString());
            return draft;
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create draft: " + e.getDetails());
            throw e;
        }
    }
}
// [END gmail_create_draft_with_attachment]