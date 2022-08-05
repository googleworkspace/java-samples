/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.vault.chatmigration;

import com.github.rholder.retry.RetryException;
import com.google.api.services.vault.v1.Vault;
import com.google.api.services.vault.v1.model.CorpusQuery;
import com.google.api.services.vault.v1.model.HeldAccount;
import com.google.api.services.vault.v1.model.HeldHangoutsChatQuery;
import com.google.api.services.vault.v1.model.HeldOrgUnit;
import com.google.api.services.vault.v1.model.Hold;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class DuplicateHold {
  public static final String HOLD_NAME_SUFFIX = "_hangoutsChat";
  private static final String ERROR_DESCRIPTION = "Error Description";
  private static final int MAX_ACCOUNTS_FOR_HOLD = 100;
  private static Logger logger = Logger.getLogger(DuplicateHold.class.getName());
  Vault vaultService;
  private CSVParser csvParser;
  private boolean includeRooms;
  private CSVPrinter errorReport;
  private int numberOfHolds = 0;

  public DuplicateHold(
      CSVParser csvParser, boolean includeRooms, Vault vaultService, CSVPrinter errorReport) {
    this.csvParser = csvParser;
    this.errorReport = errorReport;
    this.includeRooms = includeRooms;
    this.vaultService = vaultService;
  }

  public int duplicateHolds() throws Exception {
    writeHeader();
    for (CSVRecord record : csvParser.getRecords()) {
      String matterId = record.get(HoldsReport.MATTER_ID);
      String name = record.get(HoldsReport.HOLD_NAME);
      String orgUnitId = record.get(HoldsReport.ORG_UNIT_ID);
      String accounts = record.get(HoldsReport.ACCOUNT_IDS);

      List<HeldAccount> accountList =
          (accounts.equals(""))
              ? null
              : Arrays.stream(accounts.split(","))
              .map(account -> new HeldAccount().setAccountId(account))
              .collect(Collectors.toList());
      boolean exceedsAccountLimit = false;

      Hold hold = new Hold().setName(name + HOLD_NAME_SUFFIX).setCorpus("HANGOUTS_CHAT");

      if (!orgUnitId.equals("")) {
        hold.setOrgUnit(new HeldOrgUnit().setOrgUnitId(orgUnitId));
      } else if (!"".equals(accounts)) {
        if (accountList.size() > MAX_ACCOUNTS_FOR_HOLD) {
          exceedsAccountLimit = true;
          hold.setAccounts(accountList.subList(0, MAX_ACCOUNTS_FOR_HOLD));
        } else {
          hold.setAccounts(accountList);
        }
      }
      hold.setQuery(
          new CorpusQuery()
              .setHangoutsChatQuery(new HeldHangoutsChatQuery().setIncludeRooms(includeRooms)));
      try {
        Hold response =
            RetryableTemplate.callWithRetry(
                () -> vaultService.matters().holds().create(matterId, hold).execute());

        if (exceedsAccountLimit) {
          addAccountsToHold(
              matterId,
              response.getHoldId(),
              accountList.subList(MAX_ACCOUNTS_FOR_HOLD, accountList.size()));
        }

        numberOfHolds++;
        System.out.println("Created hold: '" + hold.getName() + "'");
        logger.log(Level.INFO, "Copied '" + hold.getName() + "'");
      } catch (ExecutionException e) {
        writeError(record, e);
      } catch (RetryException e) {
        writeError(record, e);
      }
    }
    csvParser.close();
    errorReport.close();

    return numberOfHolds;
  }

  private void writeHeader() throws IOException {
    errorReport.printRecord(
        HoldsReport.MATTER_ID,
        HoldsReport.MATTER_ID,
        HoldsReport.HOLD_ID,
        HoldsReport.HOLD_NAME,
        ERROR_DESCRIPTION);
  }

  private void writeError(CSVRecord record, Exception ex) throws IOException {
    System.out.println(
        "Hold: '" + record.get(HoldsReport.HOLD_NAME) + "' not copied to Hangouts chat.");

    errorReport.printRecord(
        record.get(HoldsReport.MATTER_ID),
        record.get(HoldsReport.MATTER_NAME),
        record.get(HoldsReport.HOLD_ID),
        record.get(HoldsReport.HOLD_NAME),
        ex.getMessage());
  }

  private void addAccountsToHold(String matterId, String holdId, List<HeldAccount> accounts)
      throws ExecutionException, RetryException {
    logger.log(
        Level.INFO,
        "There are more than 100 users on hold: " + holdId + " in matter: " + matterId + ".");
    for (HeldAccount account : accounts) {
      logger.log(Level.INFO, "Adding account: " + account.getAccountId() + " to hold: " + holdId);
      RetryableTemplate.callWithRetry(
          () ->
              vaultService
                  .matters()
                  .holds()
                  .accounts()
                  .create(matterId, holdId, account)
                  .execute());
    }
  }
}
