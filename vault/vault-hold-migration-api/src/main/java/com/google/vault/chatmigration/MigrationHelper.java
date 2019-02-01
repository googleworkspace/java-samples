package com.google.vault.chatmigration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.vault.v1.Vault;
import com.google.api.services.vault.v1.VaultScopes;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class MigrationHelper {

  enum MigrationOptions {
    GENERATE_REPORT("a", "genholdreport", "Generate Hold Report"),
    DUPLICATE_HOLDS("b", "duplicateholds", "Duplicate Gmail Holds to Hangouts Chat"),
    REPORT_FILE("f", "reportfile", "Path to holds report file"),
    ERROR_FILE("e", "errorfile", "Path to error report file"),
    INCLUDE_ROOMS("g", "includerooms", "Include Rooms when duplicating holds to Hangouts Chat"),
    HELP("h", "help", "Options Help");

    private final String option;
    private final String longOpt;
    private final String description;

    MigrationOptions(String opt, String longOpt, String description) {
      this.option = opt;
      this.longOpt = longOpt;
      this.description = description;
    }

    public String getOption() {
      return option;
    }
  }

  /** Application name. */
  private static final String APPLICATION_NAME = "Google Vault API Java Quickstart";

  /** Directory to store authorization tokens for this application. */
  private static final java.io.File DATA_STORE_DIR = new java.io.File("tokens");

  /** Global instance of the {@link FileDataStoreFactory}. */
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /**
   * Global instance of the scopes required by this quickstart.
   *
   * <p>If modifying these scopes, delete your previously saved credentials at
   * ~/.credentials/vault.googleapis.com-java-quickstart
   */
  private static final List<String> SCOPES =
      Arrays.asList(
          VaultScopes.EDISCOVERY,
          DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY,
          DirectoryScopes.ADMIN_DIRECTORY_ORGUNIT_READONLY);

  static {
    try {
      InputStream stream =
          QuickStart.class.getClassLoader().getResourceAsStream("logging.properties");
      LogManager.getLogManager().readConfiguration(stream);
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  static final Option helpOption =
      Option.builder(MigrationOptions.HELP.option)
          .longOpt(MigrationOptions.HELP.longOpt)
          .argName("help")
          .desc(MigrationOptions.HELP.description)
          .build();

  static Options buildOptions() {
    Options options = new Options();

    Option generateReport =
        Option.builder(MigrationOptions.GENERATE_REPORT.option)
            .longOpt(MigrationOptions.GENERATE_REPORT.longOpt)
            .desc(MigrationOptions.GENERATE_REPORT.description)
            .build();

    Option duplicateHolds =
        Option.builder(MigrationOptions.DUPLICATE_HOLDS.option)
            .longOpt(MigrationOptions.DUPLICATE_HOLDS.longOpt)
            .desc(MigrationOptions.DUPLICATE_HOLDS.description)
            .build();

    Option reportFile =
        Option.builder(MigrationOptions.REPORT_FILE.option)
            .required()
            .hasArg()
            .longOpt(MigrationOptions.REPORT_FILE.longOpt)
            .argName("reportfile")
            .desc(MigrationOptions.REPORT_FILE.description)
            .build();

    Option errorFile =
        Option.builder(MigrationOptions.ERROR_FILE.option)
            .required()
            .hasArg()
            .longOpt(MigrationOptions.ERROR_FILE.longOpt)
            .argName("errorfile")
            .desc(MigrationOptions.ERROR_FILE.description)
            .build();

    Option includeRoom =
        Option.builder(MigrationOptions.INCLUDE_ROOMS.option)
            .longOpt(MigrationOptions.INCLUDE_ROOMS.longOpt)
            .argName(MigrationOptions.INCLUDE_ROOMS.description)
            .desc(MigrationOptions.INCLUDE_ROOMS.description)
            .build();

    options.addOption(reportFile);
    options.addOption(errorFile);
    options.addOption(includeRoom);

    OptionGroup optionGroup = new OptionGroup();
    optionGroup.addOption(generateReport).addOption(duplicateHolds).addOption(helpOption);
    optionGroup.setRequired(true);
    options.addOptionGroup(optionGroup);

    return options;
  }

  static void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("gradle -q --args=\"<options>\"", options);
  }

  /**
   * Creates an authorized Credential object.
   *
   * @return an authorized Credential object.
   * @throws IOException
   */
  public static Credential authorize() throws IOException {
    // Load client secrets.
    InputStream in = MigrationHelper.class.getResourceAsStream("/credentials.json");
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build();
    Credential credential =
        new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
    return credential;
  }

  /**
   * Build and return an authorized Vault client service.
   *
   * @return an authorized Vault client service
   * @throws IOException
   */
  public static Vault getVaultService() throws IOException {
    Credential credential = authorize();
    return new Vault.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  public static Directory getDirectoryService() throws IOException {
    Directory service =
        new Directory.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize())
            .setApplicationName(APPLICATION_NAME)
            .build();
    return service;
  }
}
