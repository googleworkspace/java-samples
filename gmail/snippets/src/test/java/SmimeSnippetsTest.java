import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class SmimeSnippetsTest {

  private static final long CURRENT_TIME_MS = 1234567890;
  private static final LocalDateTime CURRENT_TIME =
      LocalDateTime.ofInstant(Instant.ofEpochMilli(CURRENT_TIME_MS), ZoneId.systemDefault());
  private static final String TEST_USER = "user1@example.com";

  @Mock private Gmail mockService;
  @Mock private Gmail.Users mockUsers;
  @Mock private Gmail.Users.Settings mockSettings;
  @Mock private Gmail.Users.Settings.SendAs mockSendAs;
  @Mock private Gmail.Users.Settings.SendAs.SmimeInfo mockSmimeInfo;
  @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.Delete mockDelete;
  @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.Get mockGet;
  @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.Insert mockInsert;
  @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.List mockList;
  @Mock private Gmail.Users.Settings.SendAs.SmimeInfo.SetDefault mockSetDefault;

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

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

  @After
  public void tearDown() throws IOException {
    verifyNoMoreInteractions(mockService);
    verifyNoMoreInteractions(mockUsers);
    verifyNoMoreInteractions(mockSettings);
    verifyNoMoreInteractions(mockSendAs);
    verifyNoMoreInteractions(mockSmimeInfo);
    verifyNoMoreInteractions(mockInsert);
    verifyNoMoreInteractions(mockList);
    verifyNoMoreInteractions(mockSetDefault);

    verifyZeroInteractions(mockDelete);
    verifyZeroInteractions(mockGet);
  }

  @Test
  public void testCreateSmimeInfo() {
    SmimeInfo smimeInfo = SmimeSnippets.createSmimeInfo("files/cert.p12", null /* password */);

    assertNotNull(smimeInfo);
    assertNull(smimeInfo.getEncryptedKeyPassword());
    assertNull(smimeInfo.getExpiration());
    assertNull(smimeInfo.getId());
    assertNull(smimeInfo.getIsDefault());
    assertNull(smimeInfo.getIssuerCn());
    assertNull(smimeInfo.getPem());
    assertThat(smimeInfo.getPkcs12().length(), greaterThan(0));
  }

  @Test
  public void testCreateSmimeInfoWithPassword() {
    SmimeInfo smimeInfo = SmimeSnippets.createSmimeInfo("files/cert.p12", "certpass");

    assertNotNull(smimeInfo);
    assertEquals(smimeInfo.getEncryptedKeyPassword(), "certpass");
    assertNull(smimeInfo.getExpiration());
    assertNull(smimeInfo.getId());
    assertNull(smimeInfo.getIsDefault());
    assertNull(smimeInfo.getIssuerCn());
    assertNull(smimeInfo.getPem());
    assertThat(smimeInfo.getPkcs12().length(), greaterThan(0));
  }

  @Test
  public void testCreateSmimeInfoFileNotFound() {
    SmimeInfo smimeInfo = SmimeSnippets.createSmimeInfo("files/notfound.p12", null /* password */);

    assertNull(smimeInfo);
  }

  @Test
  public void testInsertSmimeInfo() throws IOException {
    SmimeInfo insertResult = makeFakeInsertResult();
    when(mockInsert.execute()).thenReturn(insertResult);

    SmimeInfo smimeInfo = SmimeSnippets.createSmimeInfo("files/cert.p12", null /* password */);
    SmimeInfo result = SmimeSnippets.insertSmimeInfo(mockService, TEST_USER, TEST_USER, smimeInfo);

    verifySmimeApiCalled(1);
    verify(mockSmimeInfo).insert(eq(TEST_USER), eq(TEST_USER), eq(smimeInfo));
    verify(mockInsert).execute();

    assertEquals(insertResult, result);
  }

  @Test
  public void testInsertSmimeInfoError() throws IOException {
    when(mockInsert.execute()).thenThrow(IOException.class);

    SmimeInfo smimeInfo = SmimeSnippets.createSmimeInfo("files/cert.p12", null /* password */);
    SmimeInfo result = SmimeSnippets.insertSmimeInfo(mockService, TEST_USER, TEST_USER, smimeInfo);

    verifySmimeApiCalled(1);
    verify(mockSmimeInfo).insert(eq(TEST_USER), eq(TEST_USER), eq(smimeInfo));
    verify(mockInsert).execute();

    assertNull(result);
  }

  @Test
  public void testInsertSmimeFromCsv() throws IOException {
    when(mockInsert.execute()).thenReturn(makeFakeInsertResult());

    SmimeSnippets.insertCertFromCsv((u) -> mockService, "files/certs.csv");

    verifySmimeApiCalled(2);
    verify(mockSmimeInfo).insert(eq("user1@example.com"), eq("user1@example.com"), any());
    verify(mockSmimeInfo).insert(eq("user2@example.com"), eq("user2@example.com"), any());
    verify(mockInsert, times(2)).execute();
  }

  @Test
  public void testInsertSmimeFromCsvFails() throws IOException {
    when(mockInsert.execute()).thenReturn(makeFakeInsertResult());

    SmimeSnippets.insertCertFromCsv((u) -> mockService, "files/notfound.csv");
  }

  @Test
  public void testUpdateSmimeCertsNoCerts() throws IOException {
    when(mockList.execute()).thenReturn(makeFakeListResult());

    String defaultCertId =
        SmimeSnippets.updateSmimeCerts(
            mockService,
            TEST_USER,
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

    String defaultCertId =
        SmimeSnippets.updateSmimeCerts(
            mockService,
            TEST_USER,
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

    String defaultCertId =
        SmimeSnippets.updateSmimeCerts(
            mockService,
            TEST_USER,
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

    String defaultCertId =
        SmimeSnippets.updateSmimeCerts(
            mockService,
            TEST_USER,
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

    String defaultCertId =
        SmimeSnippets.updateSmimeCerts(
            mockService,
            TEST_USER,
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

    String defaultCertId =
        SmimeSnippets.updateSmimeCerts(
            mockService,
            TEST_USER,
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
        SmimeSnippets.updateSmimeCerts(
            mockService,
            TEST_USER,
            TEST_USER,
            "files/cert.p12",
            null /* certPassword */,
            CURRENT_TIME);

    verifySmimeApiCalled(1);
    verify(mockSmimeInfo).list(eq(TEST_USER), eq(TEST_USER));
    verify(mockList).execute();

    assertNull(defaultCertId);
  }

  @Test
  public void testUpdateSmimeFromCsv() throws IOException {
    when(mockList.execute()).thenReturn(makeFakeListResult());
    when(mockInsert.execute()).thenReturn(makeFakeInsertResult());

    SmimeSnippets.updateSmimeFromCsv((u) -> mockService, "files/certs.csv", CURRENT_TIME);

    verifySmimeApiCalled(9);
    verify(mockSmimeInfo).list(eq("user1@example.com"), eq("user1@example.com"));
    verify(mockSmimeInfo).list(eq("user2@example.com"), eq("user2@example.com"));
    verify(mockSmimeInfo).list(eq("user3@example.com"), eq("user3@example.com"));
    verify(mockSmimeInfo).insert(eq("user1@example.com"), eq("user1@example.com"), any());
    verify(mockSmimeInfo).insert(eq("user2@example.com"), eq("user2@example.com"), any());
    verify(mockSmimeInfo).insert(eq("user3@example.com"), eq("user3@example.com"), any());
    verify(mockSmimeInfo)
        .setDefault(eq("user1@example.com"), eq("user1@example.com"), eq("new_certificate_id"));
    verify(mockSmimeInfo)
        .setDefault(eq("user2@example.com"), eq("user2@example.com"), eq("new_certificate_id"));
    verify(mockSmimeInfo)
        .setDefault(eq("user3@example.com"), eq("user3@example.com"), eq("new_certificate_id"));
    verify(mockList, times(3)).execute();
    verify(mockInsert, times(3)).execute();
    verify(mockSetDefault, times(3)).execute();
  }

  @Test
  public void testUpdateSmimeFromCsvFails() {
    SmimeSnippets.insertCertFromCsv((u) -> mockService, "files/notfound.csv");
    // tearDown() verifies that there were no interactions with the API.
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
