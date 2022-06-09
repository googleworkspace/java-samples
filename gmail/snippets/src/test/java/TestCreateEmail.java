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


import org.junit.Test;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

// Unit testcase for gmail create email snippet
public class TestCreateEmail extends BaseTest{

    @Test
    public void createEmail() throws MessagingException, IOException {
        MimeMessage mimeMessage = CreateEmail.createEmail(RECIPIENT,
                TEST_USER,
                "test",
                "Hello!");
        assertEquals("test", mimeMessage.getSubject());
        assertEquals("Hello!", mimeMessage.getContent());
        assertEquals(RECIPIENT, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(TEST_USER, mimeMessage.getFrom()[0].toString());
    }
}
