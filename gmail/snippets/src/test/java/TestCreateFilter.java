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


import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.assertNotNull;

public class TestCreateFilter extends BaseTest{

    private Label testLabel = null;

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
    public void testCreateNewFilter() throws IOException {
        String id = CreateFilter.createNewFilter(testLabel.getId());
        assertNotNull(id);
        this.service.users().settings().filters().delete("me", id).execute();
    }
}
