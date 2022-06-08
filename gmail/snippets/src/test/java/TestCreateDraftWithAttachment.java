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


import com.google.api.services.gmail.model.Draft;
import org.junit.Test;
import javax.mail.MessagingException;
import java.io.IOException;
import static org.junit.Assert.assertNotNull;

// Unit testcase for gmail create draft with attachment snippet
public class TestCreateDraftWithAttachment extends BaseTest{

    @Test
    public void testCreateDraftWithAttachment() throws MessagingException, IOException {
        Draft draft = CreateDraftWithAttachment.createDraftMessageWithAttachment(RECIPIENT,
                TEST_USER,
                new java.io.File("files/photo.jpg"));
        assertNotNull(draft);
        this.service.users().drafts().delete(TEST_USER, draft.getId()).execute();
    }
}
