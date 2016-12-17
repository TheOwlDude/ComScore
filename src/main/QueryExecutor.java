import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Brian on 12/16/2016.
 */
public class QueryExecutor implements Comparator<QueryResultItem> {
    public List<SelectQueryField> selectFields;
    public List<QueryField> orderFields;
    public ViewingSelector selector = new AllViewingSelector();

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

    private FieldValueList getOrderByValuesForViewing(Viewing viewing) throws Exception {
        List<String> orderByValues = new ArrayList<>();
        for(QueryField qf : orderFields) {
            orderByValues.add(viewing.getStringValueForQueryField(qf));
        }
        return new FieldValueList(orderByValues);
    }

    private String getDisplayValueForViewing(Viewing viewing) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean firstField = true;
        for(SelectQueryField sqf : selectFields) {
            if (!firstField) sb.append(",");
            firstField = false;
            sb.append(viewing.getStringValueForQueryField(sqf.field));
        }
        return sb.toString();
    }


}
