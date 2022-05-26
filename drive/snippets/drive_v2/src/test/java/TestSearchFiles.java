import com.google.api.services.drive.model.File;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class TestSearchFiles extends BaseTest{
    @Test
    public void searchFiles()
            throws IOException, GeneralSecurityException {
        this.createTestBlob();
        List<File> files = SearchFile.searchFile();
        assertNotEquals(0, files.size());
    }
}
