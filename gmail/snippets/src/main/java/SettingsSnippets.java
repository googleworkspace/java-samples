import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SettingsSnippets {

    private Gmail service;

    public SettingsSnippets(Gmail service) {
        this.service = service;
    }

    public String updateSignature() throws IOException {
        Gmail gmailService = this.service;
        // [START updateSignature]
        SendAs primaryAlias = null;
        ListSendAsResponse aliases = gmailService.users().settings().sendAs().list("me").execute();
        for (SendAs alias: aliases.getSendAs()) {
            if (alias.getIsPrimary()) {
                primaryAlias = alias;
                break;
            }
        }
        SendAs aliasSettings = new SendAs().setSignature("I heart cats.");
        SendAs result = gmailService.users().settings().sendAs().patch(
                "me",
                primaryAlias.getSendAsEmail(),
                aliasSettings)
                .execute();
        System.out.println("Updated signature for " + result.getDisplayName());
        // [END updateSignature]
        return result.getSignature();
    }

    public String createFilter(String realLabelId) throws IOException {
        Gmail gmailService = this.service;
        // [START createFilter]
        String labelId = "Label_14"; // ID of the user label to add
        // [START_EXCLUDE silent]
        labelId = realLabelId;
        // [END_EXCLUDE]
        Filter filter = new Filter()
                .setCriteria(new FilterCriteria()
                        .setFrom("cat-enthusiasts@example.com"))
                .setAction(new FilterAction()
                        .setAddLabelIds(Arrays.asList(labelId))
                        .setRemoveLabelIds(Arrays.asList("INBOX")));
        Filter result = gmailService.users().settings().filters().create("me", filter).execute();
        System.out.println("Created filter " + result.getId());
        // [END createFilter]
        return result.getId();
    }

    public AutoForwarding enableForwarding(String realForwardingAddress) throws IOException {
        Gmail gmailService = this.service;
        // [START enableForwarding]
        ForwardingAddress address = new ForwardingAddress()
                .setForwardingEmail("user2@example.com");
        // [START_EXCLUDE silent]
        address.setForwardingEmail(realForwardingAddress);
        // [END_EXCLUDE]
        ForwardingAddress createAddressResult = gmailService.users().settings().forwardingAddresses()
                .create("me", address).execute();
        if (createAddressResult.getVerificationStatus().equals("accepted")) {
            AutoForwarding autoForwarding = new AutoForwarding()
                    .setEnabled(true)
                    .setEmailAddress(address.getForwardingEmail())
                    .setDisposition("trash");
            autoForwarding = gmailService.users().settings().updateAutoForwarding("me", autoForwarding).execute();
            // [START_EXCLUDE silent]
            return autoForwarding;
        }
        // [END enableForwarding]
        return null;
    }

    public VacationSettings enableAutoReply() throws IOException {
        Gmail gmailService = this.service;
        // [START enableAutoReply]
        VacationSettings vacationSettings = new VacationSettings()
                .setEnableAutoReply(true)
                .setResponseBodyHtml("I'm on vacation and will reply when I'm back in the office. Thanks!")
                .setRestrictToDomain(true)
                .setStartTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000)
                .setEndTime(LocalDateTime.now().plusDays(7).toEpochSecond(ZoneOffset.UTC) * 1000);
        VacationSettings response = gmailService.users().settings().updateVacation("me", vacationSettings).execute();
        // [END enableAutoReply]
        return response;
    }

}
