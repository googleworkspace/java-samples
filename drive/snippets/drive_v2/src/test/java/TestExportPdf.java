import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;

public class TestExportPdf extends BaseTest{
    @Test
    public void exportPdf() throws IOException, GeneralSecurityException {
        String id = createTestDocument();
        ByteArrayOutputStream out = ExportPdf.exportPdf(id);
        assertEquals("%PDF", out.toString("UTF-8").substring(0, 4));
    }
}
