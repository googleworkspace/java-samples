import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpreadsheetPivotTables {
  private static final int SHEET_ID = 1337;
  private Sheets service;

  public BatchUpdateSpreadsheetResponse pivotTable(String spreadsheetId, List<String> ranges) throws IOException {
    Sheets service = this.service;
    int targetSheetId = 123;

    Request request = new Request().setUpdateCells(new UpdateCellsRequest()
        .setRows(Collections.singletonList(new RowData().setValues(Collections.singletonList(
            new CellData().setPivotTable(
                new PivotTable().setSource(
                    new GridRange()
                        .setSheetId(SHEET_ID)
                        .setStartRowIndex(0)
                        .setEndColumnIndex(0)
                        .setEndRowIndex(101)
                        .setEndColumnIndex(8)
                ).setRows(Collections.singletonList(
                    new PivotGroup()
                        .setSourceColumnOffset(6)
                        .setShowTotals(true)
                        .setSortOrder("ASCENDING")
                )).setColumns(Collections.singletonList(
                    new PivotGroup()
                        .setSourceColumnOffset(3)
                        .setSortOrder("ASCENDING")
                        .setShowTotals(true)
                )).setValues(Collections.singletonList(
                    new PivotValue()
                        .setSummarizeFunction("COUNTA")
                        .setSourceColumnOffset(3)
                )).setValueLayout("HORIZONTAL"))
            ))
        ))
        .setStart(new GridCoordinate()
            .setSheetId(targetSheetId)
            .setRowIndex(0)
            .setColumnIndex(0)
        )
        .setFields("pivotTable")
    );

    BatchUpdateSpreadsheetRequest body =
        new BatchUpdateSpreadsheetRequest().setRequests(Collections.singletonList(request));
    BatchUpdateSpreadsheetResponse result = service.spreadsheets()
        .batchUpdate(spreadsheetId, body)
        .execute();

    // [END batchGetValues]
    return result;
  }

  public BatchUpdateSpreadsheetResponse conditionalFormatting(String spreadsheetId) throws IOException {
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
                            new ConditionValue()
                                .setUserEnteredValue("=GT($D2,median($D$2:$D$11))")
                        ))
                    )
                    .setFormat(new CellFormat().setTextFormat(
                        new TextFormat().setForegroundColor(new Color().setRed(0.8f))
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
                            new ConditionValue()
                                .setUserEnteredValue("=LT($D2,median($D$2:$D$11))")
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

    BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
    BatchUpdateSpreadsheetResponse result = service.spreadsheets()
        .batchUpdate(spreadsheetId, body)
        .execute();

    return result;
  }
}
