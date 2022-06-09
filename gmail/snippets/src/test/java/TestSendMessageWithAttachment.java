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

import com.google.api.services.gmail.model.Message;
import org.junit.Test;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertNotNull;

// Unit testcase for gmail send email with attachment snippet
public class TestSendMessageWithAttachment extends BaseTest{

    @Test
    public void testSendEmailWithAttachment() throws MessagingException,
            IOException{
        Message message = SendMessageWithAttachment.sendEmailWithAttachment(RECIPIENT,
                TEST_USER,
                new File("files/photo.jpg"));
        assertNotNull(message);
        this.service.users().messages().delete("me", message.getId()).execute();
    }
}
