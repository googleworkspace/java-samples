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
import com.google.api.services.gmail.model.ListSmimeInfoResponse;
import com.google.api.services.gmail.model.SmimeInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Unit testcase for gmail update smime certs snippet
public class TestUpdateSmimeCerts extends BaseTest{

    private static final long CURRENT_TIME_MS = 1234567890;
    private static final LocalDateTime CURRENT_TIME =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(CURRENT_TIME_MS), ZoneId.systemDefault());
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
    public void testUpdateSmimeCertsNoCerts() throws IOException {
        when(mockList.execute()).thenReturn(makeFakeListResult());

        String defaultCertId = UpdateSmimeCerts.updateSmimeCerts(TEST_USER,
                        TEST_USER,
                        null /* certFilename */,
                        null /* certPassword */,
                        CURRENT_TIME);

        verifySmimeApiCalled(1);
        verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
        verify(mockList).execute();

        assertNull(defaultCertId);
    }

    @Test
    public void testUpdateSmimeCertsNoCertsUploadNewCert() throws IOException {
        when(mockList.execute()).thenReturn(makeFakeListResult());
        when(mockInsert.execute()).thenReturn(makeFakeInsertResult());

        String defaultCertId = UpdateSmimeCerts.updateSmimeCerts(TEST_USER,
                        TEST_USER,
                        "files/cert.p12",
                        null /* certPassword */,
                        CURRENT_TIME);

        verifySmimeApiCalled(3);
        verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
        verify(mockSmimeInfo).insert(eq(TEST_USER), eq(TEST_USER), any());
        verify(mockSmimeInfo).setDefault(eq(TEST_USER), eq(TEST_USER), eq("new_certificate_id"));
        verify(mockList).execute();
        verify(mockInsert).execute();
        verify(mockSetDefault).execute();

        assertEquals(defaultCertId, "new_certificate_id");
    }

    @Test
    public void testUpdateSmimeCertsValidDefaultCertNoUpload() throws IOException {
        ListSmimeInfoResponse listResponse =
                makeFakeListResult(Arrays.asList(true), Arrays.asList(CURRENT_TIME_MS + 1));
        when(mockList.execute()).thenReturn(listResponse);

        String defaultCertId = UpdateSmimeCerts.updateSmimeCerts(TEST_USER,
                        TEST_USER,
                        "files/cert.p12",
                        null /* certPassword */,
                        CURRENT_TIME);

        verifySmimeApiCalled(1);
        verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
        verify(mockList).execute();

        assertEquals(defaultCertId, "existing_certificate_id0");
    }

    @Test
    public void testUpdateSmimeCertsExpiredDefaultCertUploadNewCert() throws IOException {
        LocalDateTime expireTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(CURRENT_TIME_MS + 2), ZoneId.systemDefault());
        ListSmimeInfoResponse listResponse =
                makeFakeListResult(Arrays.asList(true), Arrays.asList(CURRENT_TIME_MS + 1));
        when(mockList.execute()).thenReturn(listResponse);

        when(mockInsert.execute()).thenReturn(makeFakeInsertResult());

        String defaultCertId = UpdateSmimeCerts.updateSmimeCerts(TEST_USER,
                        TEST_USER,
                        "files/cert.p12",
                        null /* certPassword */,
                        expireTime);

        verifySmimeApiCalled(3);
        verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
        verify(mockSmimeInfo).insert(eq(TEST_USER), eq(TEST_USER), any());
        verify(mockSmimeInfo).setDefault(eq(TEST_USER), eq(TEST_USER), eq("new_certificate_id"));
        verify(mockList).execute();
        verify(mockInsert).execute();
        verify(mockSetDefault).execute();

        assertEquals(defaultCertId, "new_certificate_id");
    }

    @Test
    public void testUpdateSmimeCertsExpiredDefaultCertOtherCertNewDefault() throws IOException {
        ListSmimeInfoResponse listResponse =
                makeFakeListResult(
                        Arrays.asList(true, false), Arrays.asList(CURRENT_TIME_MS - 1, CURRENT_TIME_MS + 1));
        when(mockList.execute()).thenReturn(listResponse);

        String defaultCertId = UpdateSmimeCerts.updateSmimeCerts(TEST_USER,
                        TEST_USER,
                        "files/cert.p12",
                        null /* certPassword */,
                        CURRENT_TIME);

        verifySmimeApiCalled(2);
        verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
        verify(mockSmimeInfo).setDefault(eq(TEST_USER), eq(TEST_USER), eq("existing_certificate_id1"));
        verify(mockList).execute();
        verify(mockSetDefault).execute();

        assertEquals(defaultCertId, "existing_certificate_id1");
    }

    @Test
    public void testUpdateSmimeCertsNoCertsNoDefaultsChooseBestCertAsNewDefault() throws IOException {
        ListSmimeInfoResponse listResponse =
                makeFakeListResult(
                        Arrays.asList(false, false, false, false),
                        Arrays.asList(
                                CURRENT_TIME_MS + 2,
                                CURRENT_TIME_MS + 1,
                                CURRENT_TIME_MS + 4,
                                CURRENT_TIME_MS + 3));
        when(mockList.execute()).thenReturn(listResponse);

        String defaultCertId = UpdateSmimeCerts.updateSmimeCerts(TEST_USER,
                        TEST_USER,
                        "files/cert.p12",
                        null /* certPassword */,
                        CURRENT_TIME);

        verifySmimeApiCalled(2);
        verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
        verify(mockSmimeInfo).setDefault(eq(TEST_USER), eq(TEST_USER), eq("existing_certificate_id2"));
        verify(mockList).execute();
        verify(mockSetDefault).execute();

        assertEquals(defaultCertId, "existing_certificate_id2");
    }

    @Test
    public void testUpdateSmimeCertsError() throws IOException {
        when(mockList.execute()).thenThrow(IOException.class);

        String defaultCertId =
                UpdateSmimeCerts.updateSmimeCerts(TEST_USER,
                        TEST_USER,
                        "files/cert.p12",
                        null /* certPassword */,
                        CURRENT_TIME);

        verifySmimeApiCalled(1);
        verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
        verify(mockList).execute();

        assertNull(defaultCertId);
    }

    private void verifySmimeApiCalled(int numCalls) {
        verify(mockService, times(numCalls)).users();
        verify(mockUsers, times(numCalls)).settings();
        verify(mockSettings, times(numCalls)).sendAs();
        verify(mockSendAs, times(numCalls)).smimeInfo();
    }

    private ListSmimeInfoResponse makeFakeListResult(List<Boolean> isDefault, List<Long> expiration) {
        ListSmimeInfoResponse listResponse = new ListSmimeInfoResponse();
        if (isDefault == null || expiration == null) {
            return listResponse;
        }

        assertEquals(isDefault.size(), expiration.size());

        List<SmimeInfo> smimeInfoList = new ArrayList<SmimeInfo>();
        for (int i = 0; i < isDefault.size(); i++) {
            SmimeInfo smimeInfo = new SmimeInfo();
            smimeInfo.setId(String.format("existing_certificate_id%d", i));
            smimeInfo.setIsDefault(isDefault.get(i));
            smimeInfo.setExpiration(expiration.get(i));
            smimeInfoList.add(smimeInfo);
        }
        listResponse.setSmimeInfo(smimeInfoList);

        return listResponse;
    }

    private ListSmimeInfoResponse makeFakeListResult() {
        return makeFakeListResult(null /* isDefault */, null /* expiration */);
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
