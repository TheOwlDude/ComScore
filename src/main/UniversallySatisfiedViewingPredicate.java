/**
 * Created by bkcol_000 on 12/17/2016.
 *
 * Predicate that is always satisfied
 *
 */
public class UniversallySatisfiedViewingPredicate implements ViewingPredicate {
    public boolean isSelected(Viewing viewing) { return true; }
}
