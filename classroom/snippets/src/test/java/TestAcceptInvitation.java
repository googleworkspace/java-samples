// Copyright 2023 Google LLC
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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;

public class TestAcceptInvitation {

  private String invitationId = "insert_invitation_id";

  @Test
  public void testAcceptInvitation() throws GeneralSecurityException, IOException {
    AcceptInvitation.acceptInvitation(invitationId);
    Assert.assertThrows(GoogleJsonResponseException.class,
        () -> GetInvitation.getInvitation(invitationId));
  }
}
