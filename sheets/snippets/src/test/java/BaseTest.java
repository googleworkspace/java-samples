import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.Before;

public class BaseTest {
  protected Sheets service;
  protected Drive driveService;

  public GoogleCredentials getCredential() throws IOException {
    /* Load pre-authorized user credentials from the environment.
    TODO(developer) - See https://developers.google.com/identity for
     guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
            .createScoped(SheetsScopes.SPREADSHEETS,SheetsScopes.DRIVE);
    return credentials;
  }

  public Sheets buildService(GoogleCredentials credentials){
    return new Sheets.Builder(
        new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        new HttpCredentialsAdapter(credentials))
        .setApplicationName("Sheets API Snippets")
        .build();
  }

  public Drive buildDriveService(GoogleCredentials credentials) {
    return new Drive.Builder(
        new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        new HttpCredentialsAdapter(credentials))
        .setApplicationName("Sheets API Snippets")
        .build();
  }

  @Before
  public void setup() throws IOException {
    GoogleCredentials credential = getCredential();
    this.service = buildService(credential);
    this.driveService = buildDriveService(credential);
  }

  protected void deleteFileOnCleanup(String id) throws IOException {
    this.driveService.files().delete(id).execute();
  }

  protected void populateValuesWithStrings(String spreadsheetId) throws IOException {
    List<Request> requests = new ArrayList<>();
    requests.add(new Request().setRepeatCell(new RepeatCellRequest()
        .setRange(new GridRange()
            .setSheetId(0)
            .setStartRowIndex(0)
            .setEndRowIndex(10)
            .setStartColumnIndex(0)
            .setEndColumnIndex(10))
        .setCell(new CellData()
            .setUserEnteredValue(new ExtendedValue()
                .setStringValue("Hello")))
        .setFields("userEnteredValue")));
    BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest()
        .setRequests(requests);
    service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
  }

  protected void populateValuesWithNumbers(String spreadsheetId) throws IOException {
    List<Request> requests = new ArrayList<>();
    requests.add(new Request().setRepeatCell(new RepeatCellRequest()
        .setRange(new GridRange()
            .setSheetId(0)
            .setStartRowIndex(0)
            .setEndRowIndex(10)
            .setStartColumnIndex(0)
            .setEndColumnIndex(10))
        .setCell(new CellData()
            .setUserEnteredValue(new ExtendedValue()
                .setNumberValue(1337D)))
        .setFields("userEnteredValue")));
    BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest()
        .setRequests(requests);
    service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
  }
}
