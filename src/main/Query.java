import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bkcol_000 on 12/17/2016.
 */
public class Query {

    public static void main (String[] args) throws Exception {
        if (args.length < 3) {
            printUsage();
            return;
        }

        Path storeFolder = Paths.get(args[0]);
        if (!Files.exists(storeFolder) || !Files.isDirectory(storeFolder)) {
            System.out.println("storeFolder must be an existing directory");
            printUsage();
            return;
        }

        List<String> queryArgs = new ArrayList<>();
        for(int i = 1; i < args.length; ++i) {
            queryArgs.add(args[i]);
        }

        QueryExecutor executor;
        try {
            executor = QueryParser.getExecutor(queryArgs);
        }
        catch (Exception e) {
            System.out.println(String.format("Exception parsing query: %s", e.getMessage()));
            printUsage();
            return;
        }

        DataStore store = new DataStore(storeFolder);

        List<QueryResultItem> resultItems = executor.getResults(store.getReader());
        for(QueryResultItem item : resultItems) {
            System.out.println(item.displayString);
        }

    }

    private static void printUsage() {
        System.out.println("USAGE: storeFolder -s selecList  [-o orderList] [-f filterCondition]");
    }

}
