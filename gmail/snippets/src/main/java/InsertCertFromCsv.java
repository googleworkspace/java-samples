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


// [START gmail_insert_cert_from_csv]
import com.google.api.services.gmail.model.SmimeInfo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;

/* Class to demonstrate the use of Gmail Insert Certificate from CSV File */
public class InsertCertFromCsv {
    /**
     * Upload S/MIME certificates based on the contents of a CSV file.
     *
     * <p>Each row of the CSV file should contain a user ID, path to the certificate, and the
     * certificate password.
     *
     * @param csvFilename Name of the CSV file.
     */
    public static void insertCertFromCsv(String csvFilename) {
        try {
            File csvFile = new File(csvFilename);
            CSVParser parser =
                    CSVParser.parse(csvFile, java.nio.charset.StandardCharsets.UTF_8, CSVFormat.DEFAULT);
            for (CSVRecord record : parser) {
                String userId = record.get(0);
                String certFilename = record.get(1);
                String certPassword = record.get(2);
                SmimeInfo smimeInfo = CreateSmimeInfo.createSmimeInfo(certFilename,
                        certPassword);
                if (smimeInfo != null) {
                    InsertSmimeInfo.insertSmimeInfo(certFilename,
                            certPassword,
                            userId,
                            userId);
                } else {
                    System.err.printf("Unable to read certificate file for userId: %s\n", userId);
                }
            }
        } catch (Exception e) {
            System.err.printf("An error occured while reading the CSV file: %s", e);
        }
    }
}
// [END gmail_insert_cert_from_csv]