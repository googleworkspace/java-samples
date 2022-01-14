import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.model.SmimeInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

class SmimeSnippets {

  SmimeSnippets() {}

  // [START create_smime_info]
  /**
   * Create an SmimeInfo resource for a certificate from file.
   *
   * @param filename Name of the file containing the S/MIME certificate.
   * @param password Password for the certificate file, or null if the file is not
   *     password-protected.
   * @return An SmimeInfo object with the specified certificate.
   */
  public static SmimeInfo createSmimeInfo(String filename, String password) {
    SmimeInfo smimeInfo = null;
    InputStream in = null;

    try {
      File file = new File(filename);
      in = new FileInputStream(file);
      byte fileContent[] = new byte[(int) file.length()];
      in.read(fileContent);

      smimeInfo = new SmimeInfo();
      smimeInfo.setPkcs12(Base64.getUrlEncoder().encodeToString(fileContent));
      if (password != null && password.length() > 0) {
        smimeInfo.setEncryptedKeyPassword(password);
      }
    } catch (Exception e) {
      System.out.printf("An error occured while reading the certificate file: %s\n", e);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ioe) {
        System.out.printf("An error occured while closing the input stream: %s\n", ioe);
      }
    }

    return smimeInfo;
  }
  // [END create_smime_info]

  // [START insert_smime_info]
  /**
   * Upload an S/MIME certificate for the user.
   *
   * @param service Authorized GMail API service instance.
   * @param userId User's email address.
   * @param sendAsEmail The "send as" email address, or null if it should be the same as userId.
   * @param smimeInfo The SmimeInfo object containing the user's S/MIME certificate.
   * @return An SmimeInfo object with details about the uploaded certificate.
   */
  public static SmimeInfo insertSmimeInfo(
      Gmail service, String userId, String sendAsEmail, SmimeInfo smimeInfo) {
    if (sendAsEmail == null) {
      sendAsEmail = userId;
    }

    try {
      SmimeInfo results =
          service
              .users()
              .settings()
              .sendAs()
              .smimeInfo()
              .insert(userId, sendAsEmail, smimeInfo)
              .execute();
      System.out.printf("Inserted certificate, id: %s\n", results.getId());
      return results;
    } catch (IOException e) {
      System.err.printf("An error occured: %s", e);
    }

    return null;
  }
  // [END insert_smime_info]

  // [START insert_cert_from_csv]
  /**
   * A builder that returns a GMail API service instance that is authorized to act on behalf of the
   * specified user.
   */
  @FunctionalInterface
  public interface GmailServiceBuilder {
    Gmail buildGmailServiceFromUserId(String userId) throws IOException;
  }

  /**
   * Upload S/MIME certificates based on the contents of a CSV file.
   *
   * <p>Each row of the CSV file should contain a user ID, path to the certificate, and the
   * certificate password.
   *
   * @param serviceBuilder A function that returns an authorized GMail API service instance for a
   *     given user.
   * @param csvFilename Name of the CSV file.
   */
  public static void insertCertFromCsv(GmailServiceBuilder serviceBuilder, String csvFilename) {
    try {
      File csvFile = new File(csvFilename);
      CSVParser parser =
          CSVParser.parse(csvFile, java.nio.charset.StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      for (CSVRecord record : parser) {
        String userId = record.get(0);
        String certFilename = record.get(1);
        String certPassword = record.get(2);
        SmimeInfo smimeInfo = createSmimeInfo(certFilename, certPassword);
        if (smimeInfo != null) {
          insertSmimeInfo(
              serviceBuilder.buildGmailServiceFromUserId(userId), userId, userId, smimeInfo);
        } else {
          System.err.printf("Unable to read certificate file for userId: %s\n", userId);
        }
      }
    } catch (Exception e) {
      System.err.printf("An error occured while reading the CSV file: %s", e);
    }
  }
  // [END insert_cert_from_csv]

  // [START update_smime_certs]
  /**
   * Update S/MIME certificates for the user.
   *
   * <p>First performs a lookup of all certificates for a user. If there are no certificates, or
   * they all expire before the specified date/time, uploads the certificate in the specified file.
   * If the default certificate is expired or there was no default set, chooses the certificate with
   * the expiration furthest into the future and sets it as default.
   *
   * @param service Authorized GMail API service instance.
   * @param userId User's email address.
   * @param sendAsEmail The "send as" email address, or None if it should be the same as user_id.
   * @param certFilename Name of the file containing the S/MIME certificate.
   * @param certPassword Password for the certificate file, or None if the file is not
   *     password-protected.
   * @param expireTime DateTime object against which the certificate expiration is compared. If
   *     None, uses the current time. @ returns: The ID of the default certificate.
   * @return The ID of the default certifcate.
   */
  public static String updateSmimeCerts(
      Gmail service,
      String userId,
      String sendAsEmail,
      String certFilename,
      String certPassword,
      LocalDateTime expireTime) {
    if (sendAsEmail == null) {
      sendAsEmail = userId;
    }

    ListSmimeInfoResponse listResults = null;
    try {
      listResults =
          service.users().settings().sendAs().smimeInfo().list(userId, sendAsEmail).execute();
    } catch (IOException e) {
      System.err.printf("An error occurred during list: %s\n", e);
      return null;
    }

    String defaultCertId = null;
    String bestCertId = null;
    LocalDateTime bestCertExpire = LocalDateTime.MIN;

    if (expireTime == null) {
      expireTime = LocalDateTime.now();
    }
    if (listResults != null && listResults.getSmimeInfo() != null) {
      for (SmimeInfo smimeInfo : listResults.getSmimeInfo()) {
        String certId = smimeInfo.getId();
        boolean isDefaultCert = smimeInfo.getIsDefault();
        if (isDefaultCert) {
          defaultCertId = certId;
        }
        LocalDateTime exp =
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(smimeInfo.getExpiration()), ZoneId.systemDefault());
        if (exp.isAfter(expireTime)) {
          if (exp.isAfter(bestCertExpire)) {
            bestCertId = certId;
            bestCertExpire = exp;
          }
        } else {
          if (isDefaultCert) {
            defaultCertId = null;
          }
        }
      }
    }
    if (defaultCertId == null) {
      String defaultId = bestCertId;
      if (defaultId == null && certFilename != null) {
        SmimeInfo smimeInfo = createSmimeInfo(certFilename, certPassword);
        SmimeInfo insertResults = insertSmimeInfo(service, userId, sendAsEmail, smimeInfo);
        if (insertResults != null) {
          defaultId = insertResults.getId();
        }
      }

      if (defaultId != null) {
        try {
          service
              .users()
              .settings()
              .sendAs()
              .smimeInfo()
              .setDefault(userId, sendAsEmail, defaultId)
              .execute();
          return defaultId;
        } catch (IOException e) {
          System.err.printf("An error occured during setDefault: %s", e);
        }
      }
    } else {
      return defaultCertId;
    }

    return null;
  }

  /**
   * Update S/MIME certificates based on the contents of a CSV file.
   *
   * <p>Each row of the CSV file should contain a user ID, path to the certificate, and the
   * certificate password.
   *
   * @param serviceBuilder A function that returns an authorized GMail API service instance for a
   *     given user.
   * @param csvFilename Name of the CSV file.
   * @param expireTime DateTime object against which the certificate expiration is compared. If
   *     None, uses the current time.
   */
  public static void updateSmimeFromCsv(
      GmailServiceBuilder serviceBuilder, String csvFilename, LocalDateTime expireTime) {
    try {
      File csvFile = new File(csvFilename);
      CSVParser parser =
          CSVParser.parse(
              csvFile,
              java.nio.charset.StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord());
      for (CSVRecord record : parser) {
        String userId = record.get(0);
        String certFilename = record.get(1);
        String certPassword = record.get(2);
        updateSmimeCerts(
            serviceBuilder.buildGmailServiceFromUserId(userId),
            userId,
            userId,
            certFilename,
            certPassword,
            expireTime);
      }
    } catch (Exception e) {
      System.err.printf("An error occured while reading the CSV file: %s", e);
    }
  }
  // [END update_smime_certs]
}
