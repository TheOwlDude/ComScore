import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by bkcol_000 on 12/15/2016.
 */
public class FileImporterTests {

    private static Path importFolder = Paths.get(System.getProperty("java.io.tmpdir"), "TestFileImporter");

    @BeforeClass
    public static void setup() throws IOException {
        if (!Files.exists(importFolder)) Files.createDirectory(importFolder);
    }

    @Test
    public void wildcardTests()
    {
        Path wildcardPath = Paths.get(importFolder.toString(), "foo.*");

    }


    @Test
    public void parsesFileWithHeaderRow() throws Exception {
        String header = "STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME";
        String strViewing1 = "stb1|the matrix|warner bros|2014-04-01|4.00|1:30";
        String strViewing2 = "stb1|unbreakable|buena vista|2014-04-03|6.00|2:05";
        String strViewing3 = "stb2|the hobbit|warner bros|2014-04-02|8.00|2:45";
        String strViewing4 = "stb3|the matrix|warner bros|2014-04-02|4.00|1:05";

        Viewing viewing1 = new Viewing(strViewing1);
        Viewing viewing2 = new Viewing(strViewing2);
        Viewing viewing3 = new Viewing(strViewing3);
        Viewing viewing4 = new Viewing(strViewing4);

        Path importFilePath  = Paths.get(importFolder.toString(), "import");
        Files.deleteIfExists(importFilePath);

        try {
            File importFile = new File(importFilePath.toString());
            FileOutputStream fos = new FileOutputStream(importFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(header);
            bw.newLine();

            String[] viewings = new String[]{strViewing1, strViewing2, strViewing3, strViewing4};
            for (int i = 0; i < viewings.length; i++) {
                bw.write(viewings[i]);
                bw.newLine();
            }
            bw.close();
            fos.close();

            Map<ViewingKey,Viewing> importedViewings = FileImporter.importFile(importFilePath);
            Assert.assertEquals(4, importedViewings.size());
            DataStoreTests.CompareViewings(importedViewings, new Viewing[] { viewing1, viewing2, viewing3, viewing4 });
        }
        finally {
            Files.deleteIfExists(importFilePath);
        }
    }

    @Test
    public void parsesFileWithoutHeaderRow() throws Exception {
        String strViewing1 = "stb1|the matrix|warner bros|2014-04-01|4.00|1:30";
        String strViewing2 = "stb1|unbreakable|buena vista|2014-04-03|6.00|2:05";
        String strViewing3 = "stb2|the hobbit|warner bros|2014-04-02|8.00|2:45";
        String strViewing4 = "stb3|the matrix|warner bros|2014-04-02|4.00|1:05";

        Viewing viewing1 = new Viewing(strViewing1);
        Viewing viewing2 = new Viewing(strViewing2);
        Viewing viewing3 = new Viewing(strViewing3);
        Viewing viewing4 = new Viewing(strViewing4);

        Path importFilePath  = Paths.get(importFolder.toString(), "import");
        Files.deleteIfExists(importFilePath);

        try {
            File importFile = new File(importFilePath.toString());
            FileOutputStream fos = new FileOutputStream(importFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            String[] viewings = new String[]{strViewing1, strViewing2, strViewing3, strViewing4};
            for (int i = 0; i < viewings.length; i++) {
                bw.write(viewings[i]);
                bw.newLine();
            }
            bw.close();
            fos.close();

            Map<ViewingKey,Viewing> importedViewings = FileImporter.importFile(importFilePath);
            Assert.assertEquals(4, importedViewings.size());
            DataStoreTests.CompareViewings(importedViewings, new Viewing[] { viewing1, viewing2, viewing3, viewing4 });
        }
        finally {
            Files.deleteIfExists(importFilePath);
        }
    }

}
