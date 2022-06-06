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

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.SmimeInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Unit testcase for gmail insert cert from csv snippet
public class TestInsertCertFromCsv extends BaseTest{

    private static final long CURRENT_TIME_MS = 1234567890;
    public static final String TEST_USER1 = "gduser1@workspacesamples.dev";
    public static final String TEST_USER2 = "gduser2@workspacesamples.dev";

    @Mock
    private Gmail mockService;
    @Mock private Gmail.Users mockUsers;
    @Mock private Gmail.Users.Settings mockSettings;
    @Mock private Gmail.Users.Settings.SendAs mockSendAs;
    @Mock private Gmail.Users.Settings.SendAs.SmimeInfo mockSmimeInfo;
    @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.Delete mockDelete;
    @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.Get mockGet;
    @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.Insert mockInsert;
    @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.List mockList;
    @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.SetDefault mockSetDefault;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws IOException {
        when(mockService.users()).thenReturn(mockUsers);
        when(mockUsers.settings()).thenReturn(mockSettings);
        when(mockSettings.sendAs()).thenReturn(mockSendAs);
        when(mockSendAs.smimeInfo()).thenReturn(mockSmimeInfo);

        when(mockSmimeInfo.delete(any(), any(), any())).thenReturn(mockDelete);
        when(mockSmimeInfo.get(any(), any(), any())).thenReturn(mockGet);
        when(mockSmimeInfo.insert(any(), any(), any())).thenReturn(mockInsert);
        when(mockSmimeInfo.list(any(), any())).thenReturn(mockList);
        when(mockSmimeInfo.setDefault(any(), any(), any())).thenReturn(mockSetDefault);
    }

    @Test
    public void testInsertSmimeFromCsv() throws IOException {
        when(mockInsert.execute()).thenReturn(makeFakeInsertResult());
        InsertCertFromCsv.insertCertFromCsv("files/certs.csv");

        verifySmimeApiCalled(2);
        verify(mockSmimeInfo).insert(eq(TEST_USER1), eq(TEST_USER1), any());
        verify(mockSmimeInfo).insert(eq(TEST_USER2), eq(TEST_USER2), any());
        verify(mockInsert, times(2)).execute();
    }

    @Test
    public void testInsertSmimeFromCsvFails() throws IOException {
        when(mockInsert.execute()).thenReturn(makeFakeInsertResult());

        InsertCertFromCsv.insertCertFromCsv("files/notfound.csv");
    }

    private void verifySmimeApiCalled(int numCalls) {
        verify(mockService, times(numCalls)).users();
        verify(mockUsers, times(numCalls)).settings();
        verify(mockSettings, times(numCalls)).sendAs();
        verify(mockSendAs, times(numCalls)).smimeInfo();
    }

    private SmimeInfo makeFakeInsertResult(String id, boolean isDefault, long expiration) {
        SmimeInfo insertResult = new SmimeInfo();
        insertResult.setId(id);
        insertResult.setIsDefault(isDefault);
        insertResult.setExpiration(expiration);

        return insertResult;
    }

    private SmimeInfo makeFakeInsertResult() {
        return makeFakeInsertResult("new_certificate_id", false, CURRENT_TIME_MS + 1);
    }
}
