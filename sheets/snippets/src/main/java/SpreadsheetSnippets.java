import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpreadsheetSnippets {
    private Sheets service;

    public SpreadsheetSnippets(Sheets service) {
        this.service = service;
    }

    public String create(String title) throws IOException {
        Sheets service = this.service;
        // [START sheets_create]
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(title));
        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
        // [END sheets_create]
        return spreadsheet.getSpreadsheetId();
    }

    public BatchUpdateSpreadsheetResponse batchUpdate(String spreadsheetId, String title,
                                                      String find, String replacement)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_batch_update]
        List<Request> requests = new ArrayList<>();
        // Change the spreadsheet's title.
        requests.add(new Request()
                .setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(new SpreadsheetProperties()
                                .setTitle(title))
                        .setFields("title")));
        // Find and replace text.
        requests.add(new Request()
                .setFindReplace(new FindReplaceRequest()
                        .setFind(find)
                        .setReplacement(replacement)
                        .setAllSheets(true)));
        // Add additional requests (operations) ...

        BatchUpdateSpreadsheetRequest body =
                new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response =
                service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
        FindReplaceResponse findReplaceResponse = response.getReplies().get(1).getFindReplace();
        System.out.printf("%d replacements made.", findReplaceResponse.getOccurrencesChanged());
        // [END sheets_batch_update]
        return response;
    }

    public ValueRange getValues(String spreadsheetId, String range) throws IOException {
        Sheets service = this.service;
        // [START sheets_get_values]
        ValueRange result = service.spreadsheets().values().get(spreadsheetId, range).execute();
        int numRows = result.getValues() != null ? result.getValues().size() : 0;
        System.out.printf("%d rows retrieved.", numRows);
        // [END sheets_get_values]
        return result;
    }

    public BatchGetValuesResponse batchGetValues(String spreadsheetId, List<String> _ranges)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_batch_get_values]
        List<String> ranges = Arrays.asList(
                //Range names ...
        );
        // [START_EXCLUDE silent]
        ranges = _ranges;
        // [END_EXCLUDE]
        BatchGetValuesResponse result = service.spreadsheets().values().batchGet(spreadsheetId)
                .setRanges(ranges).execute();
        System.out.printf("%d ranges retrieved.", result.getValueRanges().size());
        // [END sheets_batch_get_values]
        return result;
    }
    public UpdateValuesResponse updateValues(String spreadsheetId, String range,
                                             String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_update_values]
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        // Cell values ...
                )
                // Additional rows ...
        );
        // [START_EXCLUDE silent]
        values = _values;
        // [END_EXCLUDE]
        ValueRange body = new ValueRange()
                .setValues(values);
        UpdateValuesResponse result =
                service.spreadsheets().values().update(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption)
                        .execute();
        System.out.printf("%d cells updated.", result.getUpdatedCells());
        // [END sheets_update_values]
        return result;
    }

    public BatchUpdateValuesResponse batchUpdateValues(String spreadsheetId, String range,
                                                       String valueInputOption,
                                                       List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_batch_update_values]
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        // Cell values ...
                )
                // Additional rows ...
        );
        // [START_EXCLUDE silent]
        values = _values;
        // [END_EXCLUDE]
        List<ValueRange> data = new ArrayList<ValueRange>();
        data.add(new ValueRange()
                .setRange(range)
                .setValues(values));
        // Additional ranges to update ...

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption(valueInputOption)
                .setData(data);
        BatchUpdateValuesResponse result =
                service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
        System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
        // [END sheets_batch_update_values]
        return result;
    }

    public AppendValuesResponse appendValues(String spreadsheetId, String range,
                                             String valueInputOption, List<List<Object>> _values)
            throws IOException {
        Sheets service = this.service;
        // [START sheets_append_values]
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        // Cell values ...
                )
                // Additional rows ...
        );
        // [START_EXCLUDE silent]
        values = _values;
        // [END_EXCLUDE]
        ValueRange body = new ValueRange()
                .setValues(values);
        AppendValuesResponse result =
                service.spreadsheets().values().append(spreadsheetId, range, body)
                        .setValueInputOption(valueInputOption)
                        .execute();
        System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
        // [END sheets_append_values]
        return result;
    }

    public BatchUpdateSpreadsheetResponse pivotTables(String spreadsheetId) throws IOException {
        Sheets service = this.service;

        // Create two sheets for our pivot table.
        List<Request> sheetsRequests = new ArrayList<>();
        sheetsRequests.add(new Request().setAddSheet(new AddSheetRequest()));
        sheetsRequests.add(new Request().setAddSheet(new AddSheetRequest()));

        BatchUpdateSpreadsheetRequest createSheetsBody = new BatchUpdateSpreadsheetRequest()
                .setRequests(sheetsRequests);
        BatchUpdateSpreadsheetResponse createSheetsResponse = service.spreadsheets()
                .batchUpdate(spreadsheetId, createSheetsBody).execute();
        int sourceSheetId = createSheetsResponse.getReplies().get(0).getAddSheet().getProperties()
                .getSheetId();
        int targetSheetId = createSheetsResponse.getReplies().get(1).getAddSheet().getProperties()
                .getSheetId();

        // [START sheets_pivot_tables]
        PivotTable pivotTable = new PivotTable()
                .setSource(
                        new GridRange()
                                .setSheetId(sourceSheetId)
                                .setStartRowIndex(0)
                                .setStartColumnIndex(0)
                                .setEndRowIndex(20)
                                .setEndColumnIndex(7)
                )
                .setRows(Collections.singletonList(
                        new PivotGroup()
                                .setSourceColumnOffset(1)
                                .setShowTotals(true)
                                .setSortOrder("ASCENDING")
                ))
                .setColumns(Collections.singletonList(
                        new PivotGroup()
                                .setSourceColumnOffset(4)
                                .setShowTotals(true)
                                .setSortOrder("ASCENDING")
                ))
                .setValues(Collections.singletonList(
                        new PivotValue()
                                .setSummarizeFunction("COUNTA")
                                .setSourceColumnOffset(4)
                ));
        List<Request> requests = Lists.newArrayList();
        Request updateCellsRequest = new Request().setUpdateCells(new UpdateCellsRequest()
                .setFields("*")
                .setRows(Collections.singletonList(
                        new RowData().setValues(
                                Collections.singletonList(
                                        new CellData().setPivotTable(pivotTable))
                        )
                ))
                .setStart(new GridCoordinate()
                        .setSheetId(targetSheetId)
                        .setRowIndex(0)
                        .setColumnIndex(0)

                ));

        requests.add(updateCellsRequest);
        BatchUpdateSpreadsheetRequest updateCellsBody = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
        BatchUpdateSpreadsheetResponse result = service.spreadsheets()
                .batchUpdate(spreadsheetId, updateCellsBody).execute();
        // [END sheets_pivot_tables]
        return result;
    }

    public BatchUpdateSpreadsheetResponse conditionalFormat(String spreadsheetId)
            throws IOException {
        // [START sheets_conditional_formatting]
        List<GridRange> ranges = Collections.singletonList(new GridRange()
                .setSheetId(0)
                .setStartRowIndex(1)
                .setEndRowIndex(11)
                .setStartColumnIndex(0)
                .setEndColumnIndex(4)
        );
        List<Request> requests = Arrays.asList(
                new Request().setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
                        .setRule(new ConditionalFormatRule()
                                .setRanges(ranges)
                                .setBooleanRule(new BooleanRule()
                                        .setCondition(new BooleanCondition()
                                                .setType("CUSTOM_FORMULA")
                                                .setValues(Collections.singletonList(
                                                        new ConditionValue().setUserEnteredValue(
                                                                "=GT($D2,median($D$2:$D$11))")
                                                ))
                                        )
                                        .setFormat(new CellFormat().setTextFormat(
                                                new TextFormat().setForegroundColor(
                                                        new Color().setRed(0.8f))
                                        ))
                                )
                        )
                        .setIndex(0)
                ),
                new Request().setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
                        .setRule(new ConditionalFormatRule()
                                .setRanges(ranges)
                                .setBooleanRule(new BooleanRule()
                                        .setCondition(new BooleanCondition()
                                                .setType("CUSTOM_FORMULA")
                                                .setValues(Collections.singletonList(
                                                        new ConditionValue().setUserEnteredValue(
                                                                "=LT($D2,median($D$2:$D$11))")
                                                ))
                                        )
                                        .setFormat(new CellFormat().setBackgroundColor(
                                                new Color().setRed(1f).setGreen(0.4f).setBlue(0.4f)
                                        ))
                                )
                        )
                        .setIndex(0)
                )
        );

        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
        BatchUpdateSpreadsheetResponse result = service.spreadsheets()
                .batchUpdate(spreadsheetId, body)
                .execute();
        System.out.printf("%d cells updated.", result.getReplies().size());
        // [END sheets_conditional_formatting]
        return result;
    }
}
