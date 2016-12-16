import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by bkcol_000 on 12/15/2016.
 *
 * Given a wild carded file naming pattern for paths to files matching the input file format for Viewings, returns a Map of
 * Viewinggs from the files matching the pattern
 *
 */
public class FileImporter {

    public static Map<ViewingKey, Viewing> importFile(Path importFilePath) throws FileNotFoundException, IOException, Exception {
        Map<ViewingKey, Viewing> result = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(importFilePath.toString()));
        String line;
        boolean firstLine = true;
        while((line = br.readLine()) != null) {
            //Since the example file has a header row adding some special logic to check for this and skip it if it
            //has the string VIEW_TIME in the last column. This is certainly not a duration. If this condition is not
            //met will treat the first row as data
            if (firstLine) {
                String[] components = line.split(Pattern.quote("|"));
                if (components.length == 6 && components[5].toUpperCase().equals("VIEW_TIME")) {
                    firstLine = false;
                    continue;
                }
            }
            firstLine = false;

            Viewing current = new Viewing(line);
            result.put(current.getViewingKey(), current);
        }
        br.close();
        return result;
    }
}
