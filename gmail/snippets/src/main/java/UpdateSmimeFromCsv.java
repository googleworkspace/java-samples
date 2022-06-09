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


// [START gmail_update_smime_from_csv]
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.time.LocalDateTime;

/* Class to demonstrate the use of Gmail Update Certificate from CSV File */
public class UpdateSmimeFromCsv {
    /**
     * Update S/MIME certificates based on the contents of a CSV file.
     *
     * <p>Each row of the CSV file should contain a user ID, path to the certificate, and the
     * certificate password.
     *
     * @param csvFilename Name of the CSV file.
     * @param expireTime DateTime object against which the certificate expiration is compared. If
     *     None, uses the current time.
     */
    public static void updateSmimeFromCsv(String csvFilename, LocalDateTime expireTime) {
        try {
            File csvFile = new File(csvFilename);
            CSVParser parser = CSVParser.parse( csvFile,
                            java.nio.charset.StandardCharsets.UTF_8,
                            CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord());
            for (CSVRecord record : parser) {
                String userId = record.get(0);
                String certFilename = record.get(1);
                String certPassword = record.get(2);
                UpdateSmimeCerts.updateSmimeCerts(userId,
                        userId,
                        certFilename,
                        certPassword,
                        expireTime);
            }
        } catch (Exception e) {
            System.err.printf("An error occured while reading the CSV file: %s", e);
        }
    }
}
// [END gmail_update_smime_from_csv]