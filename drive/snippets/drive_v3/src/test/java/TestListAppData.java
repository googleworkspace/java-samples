import com.google.api.services.drive.model.FileList;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotEquals;

public class TestListAppData extends BaseTest{
    @Test
    public void listAppData() throws IOException, GeneralSecurityException {
        String id = UploadAppData.uploadAppData();
        deleteFileOnCleanup(id);
        FileList files = ListAppData.listAppData();
        assertNotEquals(0, files.getFiles().size());
    }
}
