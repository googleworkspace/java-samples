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


import com.google.api.services.gmail.model.AutoForwarding;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import static org.junit.Assert.assertNotNull;

public class TestEnableForwarding extends BaseTest {

    @Test
    public void TestEnableAutoForwarding() throws IOException {
        try (MockedStatic credentials = useServiceAccount()) {
            AutoForwarding forwarding = EnableForwarding.enableAutoForwarding(FORWARDING_ADDRESS);
            assertNotNull(forwarding);
        }
    }

    @Before
    public void cleanup() {
        try {
            AutoForwarding forwarding = new AutoForwarding().setEnabled(false);
            this.service.users().settings().updateAutoForwarding("me", forwarding).execute();
            this.service.users().settings().forwardingAddresses().delete("me", FORWARDING_ADDRESS).execute();
        } catch (Exception e) {
            // Ignore -- resources might not exist
            e.printStackTrace();
        }
    }
}
