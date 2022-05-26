import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMoveFileToFolder extends BaseTest{
    @Test
    public void moveFileToFolder()
            throws IOException, GeneralSecurityException {
        String folderId = CreateFolder.createFolder();
        deleteFileOnCleanup(folderId);
        String fileId = this.createTestBlob();
        List<String> parents = MoveFileToFolder.moveFileToFolder(fileId, folderId);
        assertEquals(1, parents.size());
        assertTrue(parents.contains(folderId));
    }

}
