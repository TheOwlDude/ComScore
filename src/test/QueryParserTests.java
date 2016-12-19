import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by bkcol_000 on 12/17/2016.
 */
public class QueryParserTests {

    @Test
    public void parseSelectFields() {
        String selectFieldString = "title:min,rev,provider:max,stb:count,view_time:collect,date:sum";
        List<SchemaFieldWithGroupOperation> selectFields = QueryParser.getSelectQueryFields(selectFieldString);

        Assert.assertEquals(6, selectFields.size());
        Assert.assertEquals(SchemaField.TITLE, selectFields.get(0).field);
        Assert.assertEquals(GroupOperation.MIN, selectFields.get(0).groupOperation);
        Assert.assertEquals(SchemaField.REV, selectFields.get(1).field);
        Assert.assertNull(selectFields.get(1).groupOperation);
        Assert.assertEquals(SchemaField.PROVIDER, selectFields.get(2).field);
        Assert.assertEquals(GroupOperation.MAX, selectFields.get(2).groupOperation);
        Assert.assertEquals(SchemaField.STB, selectFields.get(3).field);
        Assert.assertEquals(GroupOperation.COUNT, selectFields.get(3).groupOperation);
        Assert.assertEquals(SchemaField.VIEW_TIME, selectFields.get(4).field);
        Assert.assertEquals(GroupOperation.COLLECT, selectFields.get(4).groupOperation);
        Assert.assertEquals(SchemaField.DATE, selectFields.get(5).field);
        Assert.assertEquals(GroupOperation.SUM, selectFields.get(5).groupOperation);
    }
}
