import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by bkcol_000 on 12/16/2016.
 *
 * class with main.
 *
 * Supports creating/updating a DataStore
 *
 */
public class Import {

    public static void main (String[] args) throws Exception {
        if (args.length < 2) {
            printUsage();
            return;
        }

        DataStore store = new DataStore(Paths.get(args[0]));
        for(int i = 1; i < args.length; ++i) {
            Map<ViewingKey,Viewing> importedViewings;
            try {
                importedViewings = FileImporter.importFile(Paths.get(args[i]));
            }
            catch(Exception e) {
                System.out.println(String.format("Exception parsing file %s: %s", args[i], e.getMessage()));
                continue;
            }

            try {
                store.AddUpdate((importedViewings));
            }
            catch (Exception e) {
                System.out.println(String.format("Exception storing data from %s: %s", args[i], e.getMessage()));
                continue;
            }
        }
    }

    private static void printUsage() {
        System.out.println("USAGE: storeFolder importFile1 importFile2 . . .");
    }
}
