/**
 * Created by bkcol_000 on 12/17/2016.
 *
 * Represents a single result row.
 *
 */
public class QueryResultItem implements Comparable<QueryResultItem>{
    public String displayString;
    private SchemaFieldValueList sortObject;

    public QueryResultItem(String displayString, SchemaFieldValueList sortObject) {
        this.displayString = displayString;
        this.sortObject = sortObject;
    }

    public int compareTo(QueryResultItem other) {
        return this.sortObject.compareTo(other.sortObject);
    }
}
