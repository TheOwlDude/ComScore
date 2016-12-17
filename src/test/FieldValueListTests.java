import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bkcol_000 on 12/17/2016.
 */
public class FieldValueListTests {

    @Test
    public void compareTo_EmptyListsCompareTo_0() {
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();

        Assert.assertEquals(0, new FieldValueList(list1).compareTo(new FieldValueList(list2)));
    }


    @Test
    public void compareTo_MultiEqualsWorks() {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");

        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");

        Assert.assertEquals(0, new FieldValueList(list1).compareTo(new FieldValueList(list2)));
    }

    @Test
    public void compareTo_MultiLessWorks() {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("a");

        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");

        Assert.assertTrue(new FieldValueList(list1).compareTo(new FieldValueList(list2)) < 0);
    }

    @Test
    public void compareTo_MultiMoreWorks() {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("x");

        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");

        Assert.assertTrue(new FieldValueList(list1).compareTo(new FieldValueList(list2)) > 0);
    }

    @Test
    public void compareTo_ThisMoreElementsWorks() {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        list1.add("d");

        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");

        Assert.assertTrue(new FieldValueList(list1).compareTo(new FieldValueList(list2)) > 0);
    }

    @Test
    public void compareTo_ThisLessElementsWorks() {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");

        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");

        Assert.assertTrue(new FieldValueList(list1).compareTo(new FieldValueList(list2)) < 0);
    }


    @Test
    public void hashCodeEqualsWork() {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        FieldValueList fvl1 = new FieldValueList(list1);

        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("c");
        FieldValueList fvl2 = new FieldValueList(list2);

        List<String> list3 = new ArrayList<>();
        list3.add("a");
        list3.add("b");
        list3.add("d");
        FieldValueList fvl3 = new FieldValueList(list3);

        Assert.assertEquals(fvl1.hashCode(), fvl2.hashCode());
        Assert.assertTrue(fvl1.equals(fvl2));
        Assert.assertTrue(fvl2.equals(fvl1));

        Assert.assertNotEquals(fvl1.hashCode(), fvl3.hashCode());
        Assert.assertFalse(fvl1.equals(fvl3));
        Assert.assertFalse(fvl3.equals(fvl1));
    }

    @Test
    public void EmptyListsAreEqual() {
        List<String> list1 = new ArrayList<>();
        FieldValueList fvl1 = new FieldValueList(list1);

        List<String> list2 = new ArrayList<>();
        FieldValueList fvl2 = new FieldValueList(list2);

        Assert.assertEquals(fvl1.hashCode(), fvl2.hashCode());
        Assert.assertTrue(fvl1.equals(fvl2));
        Assert.assertTrue(fvl2.equals(fvl1));
    }
}
