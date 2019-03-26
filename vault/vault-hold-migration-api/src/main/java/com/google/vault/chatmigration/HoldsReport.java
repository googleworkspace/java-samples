package com.google.vault.chatmigration;

import com.google.api.services.admin.directory.model.OrgUnit;
import com.google.api.services.vault.v1.Vault;
import com.google.api.services.vault.v1.model.Hold;
import com.google.api.services.vault.v1.model.ListHoldsResponse;
import com.google.api.services.vault.v1.model.ListMattersResponse;
import com.google.api.services.vault.v1.model.Matter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVPrinter;

public class HoldsReport {

  private static final Logger LOGGER = Logger.getLogger(HoldsReport.class.getName());

  public static final String MATTER_ID = "Matter Id";
  public static final String MATTER_NAME = "Matter Name";
  public static final String HOLD_ID = "Hold Id";
  public static final String HOLD_NAME = "Hold Name";
  public static final String ORG_UNIT_ID = "OrgUnitId";
  public static final String ORG_UNIT_PATH = "OrgUnitPath";
  public static final String ORG_UNIT_NAME = "OrgUnitName";
  public static final String ACCOUNT_IDS = "AccountIds";
  public static final String ACCOUNTS = "Accounts";
  public static final String CORPUS = "Corpus";
  public static final String TERMS = "Terms";
  public static final String START_TIME = "startTime";
  public static final String END_TIME = "endTime";

  private Vault vaultService;
  private DirectoryService directoryService;
  private CSVPrinter printer;

  private int numberOfHolds = 0;

  public HoldsReport(Vault vaultService, DirectoryService directoryService, CSVPrinter printer) {
    this.vaultService = vaultService;
    this.directoryService = directoryService;
    this.printer = printer;
  }

  public int buildReport() throws Exception {
    writeHeader();
    iterateMatters(null);
    printer.close();
    return numberOfHolds;
  }

  private void iterateMatters(String nextPageToken) throws Exception {
    // Build a new authorized API client service.
    ListMattersResponse response =
        RetryableTemplate.callWithRetry(
            vaultService
                    .matters()
                    .list()
                    .setState("OPEN")
                    .setPageSize(100)
                    .setPageToken(nextPageToken)
                ::execute);
    List<Matter> matters = response.getMatters();

    if (matters != null && matters.size() > 0) {
      for (Matter matter : matters) {
        iterateHolds(matter, null);
      }
    }
    if (response.getNextPageToken() != null) {
      iterateMatters(response.getNextPageToken());
    }
  }

  private void iterateHolds(Matter matter, String nextPageToken) throws Exception {
    ListHoldsResponse response =
        RetryableTemplate.callWithRetry(
            () ->
                vaultService
                    .matters()
                    .holds()
                    .list(matter.getMatterId())
                    .setPageSize(100)
                    .setPageToken(nextPageToken)
                    .execute());
    List<Hold> holds = response.getHolds();
    if (holds != null && holds.size() > 0) {
      for (Hold hold : holds) {
        if ("MAIL".equals(hold.getCorpus())) {
          writeHold(matter, hold);
          ++numberOfHolds;
          System.out.println("Completed " + numberOfHolds + " holds. In progress...");
          LOGGER.log(
              Level.INFO, "Completed " + numberOfHolds + " holds. Report generation in progress.");
        }
      }
    }
    if (response.getNextPageToken() != null) {
      iterateHolds(matter, response.getNextPageToken());
    }
  }

  private void writeHeader() throws IOException {
    printer.printRecord(
        MATTER_ID,
        MATTER_NAME,
        HOLD_ID,
        HOLD_NAME,
        ORG_UNIT_ID,
        ORG_UNIT_PATH,
        ORG_UNIT_NAME,
        ACCOUNT_IDS,
        ACCOUNTS,
        CORPUS,
        TERMS,
        START_TIME,
        END_TIME);
  }

  private void writeHold(Matter matter, Hold hold) throws IOException {
    if (hold.getOrgUnit() != null) {
      OrgUnit orgUnit = directoryService.getOrgUnit(hold.getOrgUnit().getOrgUnitId());
      printer.printRecord(
          matter.getMatterId(),
          matter.getName(),
          hold.getHoldId(),
          hold.getName(),
          hold.getOrgUnit().getOrgUnitId(),
          orgUnit != null ? orgUnit.getOrgUnitPath() : null,
          orgUnit != null ? orgUnit.getName() : null,
          null,
          null,
          hold.getCorpus(),
          hold.getQuery().getMailQuery().getTerms(),
          hold.getQuery().getMailQuery().getStartTime(),
          hold.getQuery().getMailQuery().getEndTime());
    } else if (hold.getAccounts() != null) {
      printer.printRecord(
          matter.getMatterId(),
          matter.getName(),
          hold.getHoldId(),
          hold.getName(),
          null,
          null,
          null,
          hold.getAccounts().stream()
              .map(account -> account.getAccountId())
              .collect(Collectors.joining(",")),
          hold.getAccounts().stream()
              .map(account -> directoryService.getEmail(account.getAccountId()))
              .collect(Collectors.joining(",")),
          hold.getCorpus(),
          hold.getQuery().getMailQuery().getTerms(),
          hold.getQuery().getMailQuery().getStartTime(),
          hold.getQuery().getMailQuery().getEndTime());
    }
    printer.flush();
  }
}
