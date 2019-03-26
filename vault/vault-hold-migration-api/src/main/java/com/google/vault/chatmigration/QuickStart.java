package com.google.vault.chatmigration;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.vault.v1.Vault;
import com.google.vault.chatmigration.MigrationHelper.MigrationOptions;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

public class QuickStart {

  private static final Logger LOGGER = Logger.getLogger(QuickStart.class.getName());

  private static boolean hasHelpOption(String... args) throws ParseException {
    boolean hasHelp = false;

    Options helpOptions = new Options().addOption(MigrationHelper.helpOption);
    CommandLine cl = new DefaultParser().parse(helpOptions, args, true);

    if (cl.hasOption(MigrationOptions.HELP.getOption())) {
      hasHelp = true;
    }

    return hasHelp;
  }

  public static void main(String... args) {
    Options options = MigrationHelper.buildOptions();

    try {

      if (hasHelpOption(args)) {
        MigrationHelper.printHelp(options);
      } else {
        CommandLine line = new DefaultParser().parse(options, args);
        String reportFile = line.getOptionValue("f");
        String errorFile = line.getOptionValue("e");

        if (line.hasOption(MigrationOptions.GENERATE_REPORT.getOption())) {
          generateReport(reportFile);
        } else if (line.hasOption(MigrationOptions.DUPLICATE_HOLDS.getOption())) {
          duplicateHolds(
              reportFile, errorFile, line.hasOption(MigrationOptions.INCLUDE_ROOMS.getOption()));
        }
      }

    } catch (ParseException parseException) {
      System.out.println(parseException.getMessage());
      LOGGER.log(Level.WARNING, parseException.toString());
      MigrationHelper.printHelp(options);
    } catch (Exception exception) {
      LOGGER.log(Level.SEVERE, exception.toString());
    }
  }

  private static void generateReport(String holdsReportFile) throws Exception {

    Directory directory = MigrationHelper.getDirectoryService();
    DirectoryService directoryService = new DirectoryService(directory);
    Vault vaultService = MigrationHelper.getVaultService();

    System.out.println(
        "--------------------------------------------------------------------------------------");
    System.out.println(
        " Starting Hold report generation. Holds will be exported to: " + holdsReportFile);
    System.out.println();

    CSVPrinter printer = getCSVPrinter(holdsReportFile);
    HoldsReport holdReport = new HoldsReport(vaultService, directoryService, printer);
    int totalHoldsCount = holdReport.buildReport();

    System.out.println();
    System.out.println(
        " Hold report generated successfully. " + totalHoldsCount + " Gmail holds exported.");
    System.out.println(
        "--------------------------------------------------------------------------------------");
  }

  private static void duplicateHolds(String holdsReportFile, String errorFile, boolean includeRooms)
      throws Exception {
    Vault vaultService = MigrationHelper.getVaultService();

    System.out.println(
        "-----------------------------------------------------------------------------------------------");
    System.out.println(
        " Hangouts Chat hold creation started. Hold(s) will be picked from: " + holdsReportFile);
    System.out.println();

    CSVPrinter errorReport = getCSVPrinter(errorFile);
    CSVParser parser =
        CSVParser.parse(
            new File(holdsReportFile),
            Charset.defaultCharset(),
            CSVFormat.DEFAULT
                .withHeader(
                    HoldsReport.MATTER_ID,
                    HoldsReport.MATTER_NAME,
                    HoldsReport.HOLD_ID,
                    HoldsReport.HOLD_NAME,
                    HoldsReport.ORG_UNIT_ID,
                    HoldsReport.ORG_UNIT_PATH,
                    HoldsReport.ORG_UNIT_NAME,
                    HoldsReport.ACCOUNT_IDS,
                    HoldsReport.ACCOUNTS,
                    HoldsReport.CORPUS,
                    HoldsReport.TERMS,
                    HoldsReport.START_TIME,
                    HoldsReport.END_TIME)
                .withSkipHeaderRecord());
    DuplicateHold duplicateHold =
        new DuplicateHold(parser, includeRooms, vaultService, errorReport);
    int holdCount = duplicateHold.duplicateHolds();

    System.out.println();
    System.out.println(" Finished creating Hangouts Chat hold(s).");
    System.out.println(
        " Copied "
            + holdCount
            + " holds for Hangouts Chat. Please check "
            + errorFile
            + " for any errors.");
    System.out.println(
        "--------------------------------------------------------------------------------------");
  }

  private static CSVPrinter getCSVPrinter(String fileName) throws IOException {
    return new CSVPrinter(new FileWriter(fileName), CSVFormat.DEFAULT);
  }
}
