/**
 * Created by bkcol_000 on 12/17/2016.
 *
 * Selector for a simple field equality condition
 *
 */
public class SingleFieldEqualityPredicate implements ViewingPredicate {

    private SingleFieldEqualityCondition leafFilterCondition;

    public SingleFieldEqualityPredicate(SingleFieldEqualityCondition leafFilterCondition) {
        this.leafFilterCondition = leafFilterCondition;
    }
    public boolean isSelected(Viewing viewing) throws Exception {
        return viewing.getStringValueForQueryField(leafFilterCondition.field).equals(leafFilterCondition.eqValue);
    }
}
