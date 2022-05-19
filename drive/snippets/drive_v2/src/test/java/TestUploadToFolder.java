import com.google.api.services.drive.model.File;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertTrue;

public class TestUploadToFolder extends BaseTest {
    @Test
    public void uploadToFolder() throws IOException, GeneralSecurityException {
        String folderId = CreateFolder.createFolder();
        File file = UploadToFolder.uploadToFolder(folderId);
        assertTrue(file.getParents().get(0).getId().equals(folderId));
        deleteFileOnCleanup(file.getId());
        deleteFileOnCleanup(folderId);
    }
}
