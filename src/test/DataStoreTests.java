import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brian on 12/15/2016.
 */
public class DataStoreTests {

    private static Path storeFolder = Paths.get(System.getProperty("java.io.tmpdir"), "TestDataStore");

    @BeforeClass
    public static void setup() throws IOException {
        if (!Files.exists(storeFolder)) Files.createDirectory(storeFolder);
    }

    @Test
    public void RoundTripThroughStore() throws IOException, Exception {
        Path masterStoreFilePath = Paths.get(storeFolder.toString(), DataStore.masterStoreFileName);
        if (Files.exists(masterStoreFilePath)) Files.delete(masterStoreFilePath);

        Viewing viewing1 = new Viewing("stb1|unbreakable|buena vista|2014-04-03|6.00|2:05");
        Viewing viewing2 = new Viewing("stb2|the hobbit|warner bros|2014-04-02|8.00|2:45");

        Map<ViewingKey,Viewing> map = new HashMap<>();
        map.put(viewing1.getViewingKey(), viewing1);
        map.put(viewing2.getViewingKey(), viewing2);

        DataStore store = new DataStore(storeFolder);

        store.AddUpdate(map);

        DataStore.Reader stream = store.getReader();
        Viewing fromStore1 = stream.getNextViewing();
        Assert.assertNotNull(fromStore1);
        Viewing fromStore2 = stream.getNextViewing();
        Assert.assertNotNull(fromStore2);
        Viewing fromStore3 = stream.getNextViewing();
        Assert.assertNull(fromStore3);
        stream.close();

        CompareViewings(map, new Viewing[] { fromStore1, fromStore2 });
    }

    public static void CompareViewings(Map<ViewingKey,Viewing> originalViewings, Viewing[] fromStoreViewings) {
        for(int i = 0; i < fromStoreViewings.length ; ++i) {
            ViewingKey fromStroreKey = fromStoreViewings[i].getViewingKey();
            Viewing originalViewing = originalViewings.get(fromStroreKey);
            Assert.assertEquals(originalViewing.setTopBoxId, fromStoreViewings[i].setTopBoxId);
            Assert.assertEquals(originalViewing.provider, fromStoreViewings[i].provider);
            Assert.assertEquals(originalViewing.title, fromStoreViewings[i].title);
            Assert.assertEquals(originalViewing.viewDate, fromStoreViewings[i].viewDate);
            Assert.assertEquals(originalViewing.revenueInCents, fromStoreViewings[i].revenueInCents);
            Assert.assertEquals(originalViewing.viewDurationInMinutes, fromStoreViewings[i].viewDurationInMinutes);
        }

    }


    @Test
    public void AddNewToExistingStore() throws IOException, Exception {
        Path masterStoreFilePath = Paths.get(storeFolder.toString(), DataStore.masterStoreFileName);
        if (Files.exists(masterStoreFilePath)) Files.delete(masterStoreFilePath);

        Viewing viewing1 = new Viewing("stb1|unbreakable|buena vista|2014-04-03|6.00|2:05");
        Viewing viewing2 = new Viewing("stb2|the hobbit|warner bros|2014-04-02|8.00|2:45");

        Map<ViewingKey,Viewing> map = new HashMap<>();
        map.put(viewing1.getViewingKey(), viewing1);
        map.put(viewing2.getViewingKey(), viewing2);

        DataStore store = new DataStore(storeFolder);

        store.AddUpdate(map);

        Viewing viewing3 = new Viewing("stb1|the matrix|warner bros|2014-04-01|4.00|1:30");
        Viewing viewing4 = new Viewing("stb3|the matrix|warner bros|2014-04-02|4.00|1:05");

        Map<ViewingKey,Viewing> mapAdd = new HashMap<>();
        mapAdd.put(viewing3.getViewingKey(), viewing3);
        mapAdd.put(viewing4.getViewingKey(), viewing4);

        store.AddUpdate(mapAdd);

        DataStore.Reader stream = store.getReader();

        Viewing fromStore1 = stream.getNextViewing();
        Assert.assertNotNull(fromStore1);
        Viewing fromStore2 = stream.getNextViewing();
        Assert.assertNotNull(fromStore2);
        Viewing fromStore3 = stream.getNextViewing();
        Assert.assertNotNull(fromStore3);
        Viewing fromStore4 = stream.getNextViewing();
        Assert.assertNotNull(fromStore4);
        Viewing fromStore5 = stream.getNextViewing();
        Assert.assertNull(fromStore5);
        stream.close();


        map.putAll(mapAdd);
        CompareViewings(map, new Viewing[] { fromStore1, fromStore2, fromStore3, fromStore4 });

    }

    @Test
    public void AddsAndUpdatesExistingStore() throws IOException, Exception {
        Path masterStoreFilePath = Paths.get(storeFolder.toString(), DataStore.masterStoreFileName);
        if (Files.exists(masterStoreFilePath)) Files.delete(masterStoreFilePath);

        Viewing viewing1 = new Viewing("stb1|unbreakable|buena vista|2014-04-03|6.00|2:05");
        Viewing viewing2 = new Viewing("stb2|the hobbit|warner bros|2014-04-02|8.00|2:45");

        Map<ViewingKey,Viewing> map = new HashMap<>();
        map.put(viewing1.getViewingKey(), viewing1);
        map.put(viewing2.getViewingKey(), viewing2);

        DataStore store = new DataStore(storeFolder);

        store.AddUpdate(map);

        Viewing viewing3 = new Viewing("stb1|unbreakable|buena vista|2014-04-03|123.00|5:30");
        Viewing viewing4 = new Viewing("stb3|the matrix|warner bros|2014-04-02|4.00|1:05");

        Map<ViewingKey,Viewing> mapAddUpate = new HashMap<>();
        mapAddUpate.put(viewing3.getViewingKey(), viewing3);
        mapAddUpate.put(viewing4.getViewingKey(), viewing4);

        store.AddUpdate(mapAddUpate);

        DataStore.Reader stream = store.getReader();

        Viewing fromStore1 = stream.getNextViewing();
        Assert.assertNotNull(fromStore1);
        Viewing fromStore2 = stream.getNextViewing();
        Assert.assertNotNull(fromStore2);
        Viewing fromStore3 = stream.getNextViewing();
        Assert.assertNotNull(fromStore3);
        Viewing fromStore4 = stream.getNextViewing();
        Assert.assertNull(fromStore4);
        stream.close();

        //viewing3 has replaced viewing1
        Map<ViewingKey,Viewing> newMap = new HashMap<>();
        newMap.put(viewing2.getViewingKey(), viewing2);
        newMap.put(viewing3.getViewingKey(), viewing3);
        newMap.put(viewing4.getViewingKey(), viewing4);

        CompareViewings(newMap, new Viewing[] { fromStore1, fromStore2, fromStore3 });

    }


    @Test
    public void updatesAreLockedWithStreamOpen() throws IOException, Exception {
        Path masterStoreFilePath = Paths.get(storeFolder.toString(), DataStore.masterStoreFileName);
        if (Files.exists(masterStoreFilePath)) Files.delete(masterStoreFilePath);

        Viewing viewing1 = new Viewing("stb1|unbreakable|buena vista|2014-04-03|6.00|2:05");
        Viewing viewing2 = new Viewing("stb2|the hobbit|warner bros|2014-04-02|8.00|2:45");

        Map<ViewingKey,Viewing> map = new HashMap<>();
        map.put(viewing1.getViewingKey(), viewing1);
        map.put(viewing2.getViewingKey(), viewing2);

        DataStore store = new DataStore(storeFolder);

        store.AddUpdate(map);

        //store is locked because stream is open
        DataStore.Reader stream = store.getReader();

        //try to update on another thread
        Viewing viewing3 = new Viewing("stb1|the matrix|warner bros|2014-04-01|4.00|1:30");
        Viewing viewing4 = new Viewing("stb3|the matrix|warner bros|2014-04-02|4.00|1:05");

        Map<ViewingKey,Viewing> mapAdd = new HashMap<>();
        mapAdd.put(viewing3.getViewingKey(), viewing3);
        mapAdd.put(viewing4.getViewingKey(), viewing4);

        DataStoreUpdateThread thread = new DataStoreUpdateThread(store, mapAdd);
        thread.start();  //the new thread is trying to add more data but can't because the store is locked.

        Thread.sleep(250);  //give the other thread a little time just to make sure it really is blocked.


        Viewing fromStore1 = stream.getNextViewing();
        Assert.assertNotNull(fromStore1);
        Viewing fromStore2 = stream.getNextViewing();
        Assert.assertNotNull(fromStore2);
        Viewing fromStore3 = stream.getNextViewing();
        Assert.assertNull(fromStore3);
        stream.close();  //the other thread is unblocked now

        CompareViewings(map, new Viewing[] { fromStore1, fromStore2 });

        Thread.sleep(250);  //give the other thread a little time to finish writing the extra data.

        stream = store.getReader();  //now reopen we will see all 4 viewings

        fromStore1 = stream.getNextViewing();
        Assert.assertNotNull(fromStore1);
        fromStore2 = stream.getNextViewing();
        Assert.assertNotNull(fromStore2);
        fromStore3 = stream.getNextViewing();
        Assert.assertNotNull(fromStore3);
        Viewing fromStore4 = stream.getNextViewing();
        Assert.assertNotNull(fromStore4);
        Viewing fromStore5 = stream.getNextViewing();
        Assert.assertNull(fromStore5);
        stream.close();


        map.putAll(mapAdd);
        CompareViewings(map, new Viewing[] { fromStore1, fromStore2, fromStore3, fromStore4 });


    }

    public class DataStoreUpdateThread extends Thread {

        private DataStore store;
        private Map<ViewingKey, Viewing> map;

        public DataStoreUpdateThread(DataStore store, Map<ViewingKey, Viewing> map) {
            this.store = store;
            this.map = map;
            this.setName("Locked out updater");
        }

        @Override
        public void run() {
            try {
                store.AddUpdate(map);
            } catch (Exception e) {
                boolean anException = true;
            }
        }
    }
}
