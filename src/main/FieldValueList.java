import java.util.List;

/**
 * Created by bkcol_000 on 12/17/2016.
 *
 * 
 */
public class FieldValueList implements Comparable<FieldValueList> {
    private List<String> fieldValues;

    public FieldValueList(List<String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for(int i = 0; i < fieldValues.size(); ++i) {
            result ^= fieldValues.get(i).hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if((other == null) || (getClass() != other.getClass())) return false;
        FieldValueList otherAsGroupingKey = (FieldValueList) other;
        if (this.fieldValues.size() != otherAsGroupingKey.fieldValues.size()) return false;
        for(int i = 0; i < fieldValues.size(); ++i) {
            if (!this.fieldValues.get(i).equals(otherAsGroupingKey.fieldValues.get(i))) return false;
        }
        return true;
    }

    public int compareTo(FieldValueList other) {
        for(int i = 0; i < this.fieldValues.size(); ++i) {
            //if they are equal up to the number of elements of other and this has more than greater
            if (i >= other.fieldValues.size()) return 1;
            int currentIndexResult = this.fieldValues.get(i).compareTo(other.fieldValues.get(i));
            if (currentIndexResult != 0) return currentIndexResult;
        }
        //we are here so that means we are equal through the elements of this, if other has more than less
        if (other.fieldValues.size() > this.fieldValues.size()) return -1;

        return 0;
    }
}
