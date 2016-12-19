import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bkcol_000 on 12/18/2016.
 */
public class QueryExecutorTests {
    private static Path storeFolder = Paths.get(System.getProperty("java.io.tmpdir"), "TestDataStore");
    private static DataStore store;

    @BeforeClass
    public static void setup() throws IOException, Exception {
        if (!Files.exists(storeFolder)) Files.createDirectory(storeFolder);
        Path masterStoreFilePath = Paths.get(storeFolder.toString(), DataStore.masterStoreFileName);
        if (Files.exists(masterStoreFilePath)) Files.delete(masterStoreFilePath);

        store = new DataStore(storeFolder);

        List<String> importRecords = new ArrayList<>();
        importRecords.add("stb1|the matrix|warner bros|2014-04-01|4.00|1:30");
        importRecords.add("stb1|unbreakable|buena vista|2014-04-03|6.00|2:05");
        importRecords.add("stb2|the hobbit|warner bros|2014-04-02|8.00|2:45");
        importRecords.add("stb3|the matrix|warner bros|2014-04-02|4.00|1:05");
        importRecords.add("stb2|the man from France|emmarah|2016-04-01|4.00|1:30");
        importRecords.add("stb4|bucket list|emmarah|2016-04-03|6.00|2:05");

        Map<ViewingKey,Viewing> importViewings = new HashMap<>();
        for(String viewingString : importRecords) {
            Viewing viewing = new Viewing(viewingString);
            importViewings.put(viewing.getViewingKey(), viewing);
        }
        store.AddUpdate((importViewings));
    }

    @Test
    public void filterBy_STB() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-f");
        queryParms.add("sTB=stb3");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(1, items.size());
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(0).displayString);
    }

    @Test
    public void filterBy_Title() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-f");
        queryParms.add("tItLe=the matrix");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(2, items.size());
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(0).displayString);
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(1).displayString);
    }

    @Test
    public void filterBy_Provider() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-f");
        queryParms.add("proVider=emmarah");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(2, items.size());
        Assert.assertEquals("4.00,emmarah,stb2,1:30,the man from France,2016-04-01", items.get(1).displayString);
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(0).displayString);
    }

    @Test
    public void filterBy_Date() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-f");
        queryParms.add("date=2016-04-03");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(1, items.size());
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(0).displayString);
    }

    @Test
    public void filterBy_Revenue() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-f");
        queryParms.add("rEV=8.00");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(1, items.size());
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(0).displayString);

    }

    @Test
    public void filterBy_View_Time() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-f");
        queryParms.add("viEW_TIme=2:45");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(1, items.size());
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(0).displayString);
    }

    @Test
    public void orderBy_stb() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-o");
        queryParms.add("sTB");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(6, items.size());
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(0).displayString);
        Assert.assertEquals("6.00,buena vista,stb1,2:05,unbreakable,2014-04-03", items.get(1).displayString);
        Assert.assertEquals("4.00,emmarah,stb2,1:30,the man from France,2016-04-01", items.get(2).displayString);
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(3).displayString);
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(4).displayString);
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(5).displayString);
    }

    @Test
    public void orderBy_title() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-o");
        queryParms.add("tItLE");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(6, items.size());
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(0).displayString);
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(1).displayString);
        Assert.assertEquals("4.00,emmarah,stb2,1:30,the man from France,2016-04-01", items.get(2).displayString);
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(3).displayString);
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(4).displayString);
        Assert.assertEquals("6.00,buena vista,stb1,2:05,unbreakable,2014-04-03", items.get(5).displayString);
    }

    @Test
    public void orderBy_provider() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-o");
        queryParms.add("PrOvIdeR,view_TiMe");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(6, items.size());
        Assert.assertEquals("6.00,buena vista,stb1,2:05,unbreakable,2014-04-03", items.get(0).displayString);
        Assert.assertEquals("4.00,emmarah,stb2,1:30,the man from France,2016-04-01", items.get(1).displayString);
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(2).displayString);
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(3).displayString);
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(4).displayString);
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(5).displayString);
    }

    @Test
    public void orderBy_date() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-o");
        queryParms.add("dAtE,stB");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(6, items.size());
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(0).displayString);
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(1).displayString);
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(2).displayString);
        Assert.assertEquals("6.00,buena vista,stb1,2:05,unbreakable,2014-04-03", items.get(3).displayString);
        Assert.assertEquals("4.00,emmarah,stb2,1:30,the man from France,2016-04-01", items.get(4).displayString);
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(5).displayString);
    }

    @Test
    public void orderBy_revenue() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-o");
        queryParms.add("reV,stB");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(6, items.size());
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(0).displayString);
        Assert.assertEquals("4.00,emmarah,stb2,1:30,the man from France,2016-04-01", items.get(1).displayString);
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(2).displayString);
        Assert.assertEquals("6.00,buena vista,stb1,2:05,unbreakable,2014-04-03", items.get(3).displayString);
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(4).displayString);
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(5).displayString);
    }

    @Test
    public void orderBy_view_time() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-o");
        queryParms.add("view_Time,stB");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(6, items.size());
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(0).displayString);
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(1).displayString);
        Assert.assertEquals("4.00,emmarah,stb2,1:30,the man from France,2016-04-01", items.get(2).displayString);
        Assert.assertEquals("6.00,buena vista,stb1,2:05,unbreakable,2014-04-03", items.get(3).displayString);
        Assert.assertEquals("6.00,emmarah,stb4,2:05,bucket list,2016-04-03", items.get(4).displayString);
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(5).displayString);
    }

    @Test
    public void orderAndFilterTogerther() throws Exception {
        List<String> queryParms = new ArrayList<>();
        queryParms.add("-s");
        queryParms.add("rEv,provIder,sTb,view_Time,tiTLE,DatE");
        queryParms.add("-o");
        queryParms.add("date,rev");
        queryParms.add("-f");
        queryParms.add("provider=warner bros");
        QueryExecutor executor = QueryParser.getExecutor(queryParms);

        List<QueryResultItem> items = executor.getResults(store.getReader());

        Assert.assertEquals(3, items.size());
        Assert.assertEquals("4.00,warner bros,stb1,1:30,the matrix,2014-04-01", items.get(0).displayString);
        Assert.assertEquals("4.00,warner bros,stb3,1:05,the matrix,2014-04-02", items.get(1).displayString);
        Assert.assertEquals("8.00,warner bros,stb2,2:45,the hobbit,2014-04-02", items.get(2).displayString);
    }

}
