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
        if (args.size() < 2) throw new Exception("At least 2 query arguments are required");

        QueryExecutor query = new QueryExecutor();
        query.orderFields = new ArrayList<>();  //initialize to empty list. Will get overwritten if requested

        boolean foundSelectArg = false;


        //Ordering of query operations is enforced, -s is required and must be first.
        int i = 0;
        if (!args.get(i++).toLowerCase().equals("-s")) {
            throw new Exception ("-s selectList is required");
        }
        query.selectFields = getSelectQueryFields(args.get(i++));

        //Parse -o if present
        //-o is optional and must precede -f if both are present.
        //specifying -f prior to -o manifests as a filter parse error. The -o token is unexpected during filter parsing
        if (i < args.size() && args.get(i).toLowerCase().equals("-o")) {
            i++; //clear -o
            String[] orderByFieldStrings = args.get(i++).split(",");
            List<SchemaField> orderByFields = new ArrayList<>();
            for (int j = 0; j < orderByFieldStrings.length; ++j) {
                orderByFields.add(SchemaField.valueOf(orderByFieldStrings[j].toUpperCase()));
            }
            query.orderFields = orderByFields;
        }

        //Parse -f if present
        if (i < args.size()) {
            String filterArg = args.get(i++);
            if (!filterArg.toLowerCase().equals("-f")) {
                throw new Exception("Expecting -f");
            }
            query.selector = new SingleFieldEqualityPredicate(parseLeafFilter(args.get(i++)));

            if (args.size() > i) {
                throw new UnsupportedOperationException("Advanced filters are not yet supported");
            }
        }

        return query;
    }


    public static List<SchemaFieldWithGroupOperation> getSelectQueryFields(String selectFields) {
        List<SchemaFieldWithGroupOperation> result = new ArrayList<>();

        String[] splitFields = selectFields.split(",");
        for(int i = 0; i < splitFields.length; ++i) {
            String[] queryFieldParts = splitFields[i].split(":");
            SchemaFieldWithGroupOperation gqf = new SchemaFieldWithGroupOperation();
            gqf.field = SchemaField.valueOf(queryFieldParts[0].toUpperCase());
            if (queryFieldParts.length > 1) gqf.groupOperation = GroupOperation.valueOf(queryFieldParts[1].toUpperCase());
            result.add(gqf);
        }
        return result;
    }

    public static SingleFieldEqualityCondition parseLeafFilter(String filterCondition) throws Exception {
        int eqPos = filterCondition.indexOf('=');
        if (eqPos == -1) throw new Exception("Failed to parse filter condition");
        SingleFieldEqualityCondition result = new SingleFieldEqualityCondition();
        result.field = SchemaField.valueOf(filterCondition.substring(0, eqPos).toUpperCase());
        result.eqValue = filterCondition.substring(eqPos + 1);
        return result;
    }


}
