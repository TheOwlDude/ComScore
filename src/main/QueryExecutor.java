import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Brian on 12/16/2016.
 *
 * Stores the parsed representation of a query
 *
 * Is able to create the result set for the query given a DataStore.Reader
 *
 */
public class QueryExecutor implements Comparator<QueryResultItem> {
    public List<SchemaFieldWithGroupOperation> selectFields;
    public List<SchemaField> orderFields;
    public ViewingPredicate selector = new UniversallySatisfiedViewingPredicate();

    public int compare(QueryResultItem o1, QueryResultItem o2) {
        return o1.compareTo(o2);
    }

    public List<QueryResultItem> getResults(DataStore.Reader reader) throws Exception{
        if (selectFields.stream().filter(sqf -> sqf.groupOperation != null).count() > 0) {
            throw new UnsupportedOperationException("Aggregate queries are not yet supported");
        }
        else {
            return getResultsUnGrouped(reader);
        }
    }

    private List<QueryResultItem> getResultsUnGrouped(DataStore.Reader reader) throws Exception {
        List<QueryResultItem> results = new ArrayList<QueryResultItem>();
        Viewing viewing;
        while((viewing = reader.getNextViewing()) != null) {
            if (selector.isSelected(viewing)) {
                results.add(
                    new QueryResultItem(
                        getDisplayValueForViewing(viewing),
                        getOrderByValuesForViewing(viewing)
                    )
                );
            }
        }
        results.sort(this);
        return results;
    }

    private SchemaFieldValueList getOrderByValuesForViewing(Viewing viewing) throws Exception {
        List<String> orderByValues = new ArrayList<>();
        for(SchemaField qf : orderFields) {
            orderByValues.add(viewing.getStringValueForQueryField(qf));
        }
        return new SchemaFieldValueList(orderByValues);
    }

    private String getDisplayValueForViewing(Viewing viewing) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean firstField = true;
        for(SchemaFieldWithGroupOperation sqf : selectFields) {
            if (!firstField) sb.append(",");
            firstField = false;
            sb.append(viewing.getStringValueForQueryField(sqf.field));
        }
        return sb.toString();
    }


}
