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


// [START gmail_create_smime_info]
import com.google.api.services.gmail.model.SmimeInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/* Class to demonstrate the use of Gmail Create SmimeInfo API */
public class CreateSmimeInfo {
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
            byte[] fileContent = new byte[(int) file.length()];
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
}
// [END gmail_create_smime_info]