/**
 * Created by bkcol_000 on 12/17/2016.
 *
 * Predicate interface for filters.
 *
 */
public interface ViewingSelector {
    boolean isSelected(Viewing viewing) throws Exception;
}
