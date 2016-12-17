import java.util.ArrayList;
import java.util.List;

/**
 * Created by bkcol_000 on 12/17/2016.
 *
 * Takes the query related command line arguments and returns a query object that describes the desired result
 *
 * This implementation is ignoring the -g flag. It seems redundant, the fields to group by are the ones without
 * aggregate operations
 *
 */
public class QueryParser {

    public static QueryExecutor getExecutor(List<String> args) throws Exception {
        if (args.size() % 2 != 0) throw new Exception("Expected even number of arguments");
        if (args.size() > 6) throw new Exception("No more than 6 query arguments are defined");
        if (args.size() < 2) throw new Exception("At least 2 query arguments are required");

        QueryExecutor query = new QueryExecutor();

        boolean foundSelectArg = false;
        for(int i = 0; i < args.size() / 2; ++i) {
            switch(args.get(2 * i).toLowerCase()) {
                case "-s":
                    foundSelectArg = true;
                    query.selectFields = getSelectQueryFields(args.get(2 * i + 1));
                    break;
                case "-o":
                    String[] orderByFieldStrings = args.get(2 * i + 1).split(",");
                    List<QueryField>  orderByFields = new ArrayList<>();
                    for(int j = 0; j < orderByFieldStrings.length; ++i) {
                        orderByFields.add(QueryField.valueOf(orderByFieldStrings[i].toUpperCase()));
                    }
                    query.orderFields = orderByFields;
                    break;
                //TODO: implement filter

            }
        }
        return query;
    }


    public static List<SelectQueryField> getSelectQueryFields(String selectFields) {
        List<SelectQueryField> result = new ArrayList<>();

        String[] splitFields = selectFields.split(",");
        for(int i = 0; i < splitFields.length; ++i) {
            String[] queryFieldParts = splitFields[i].split(":");
            SelectQueryField gqf = new SelectQueryField();
            gqf.field = QueryField.valueOf(queryFieldParts[0].toUpperCase());
            if (queryFieldParts.length > 1) gqf.groupOperation = GroupOperation.valueOf(queryFieldParts[1].toUpperCase());
            result.add(gqf);
        }
        return result;
    }


}
