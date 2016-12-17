import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Brian on 12/15/2016.
 *
 * This class handles management of the persisted store of viewings. It is not very efficient, updates require rewriting
 * the entire file. Making something more efficient seems rather challenging and would exceed the time box that is
 * reasonable for this exercise
 */
public class DataStore {
    private Path storePath;

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * This file contains the current master data
     */
    public static final String masterStoreFileName = "master";

    /**
     * This is the file that is being written during the update process
     */
    public static final String workingStoreFileName = "working";

    /**
     * After the update process is complete the working file is renamed to the pending name. Distinguishing between
     * the working and completed files will help the store recover if there is a failure moving to master. If at
     * startup there are both pending and master files present, it is expected that there was a failure deleting master
     * and in fact pending should be master. Perhaps checking the modified dates here makes sense.
     */
    public static final String pendingStoreFileName = "pending";


    public DataStore(Path storePath) {
        this.storePath = storePath;
    }


    /**
     * Returns a Reader object that can be used to iterate through the Viewings in the store. If the store file
     * @return
     * @throws FileNotFoundException
     */
    public Reader getReader() throws FileNotFoundException {
        Path masterPath = Paths.get(storePath.toString(), masterStoreFileName);
        return Files.exists(masterPath) ? new Reader(this) : null;
    }

    /**
     * The problem states that after importing many files the entire store could be too large to fit into memory. This
     * implementation assumes that a single import file can be fit into memory
     * @param importFile a dictionary created from the viewings read from an input file
     */
    public void AddUpdate(Map<ViewingKey, Viewing> importFile) throws IOException, Exception {
        lock.lock();
        try {
            Path masterPath = Paths.get(storePath.toString(), masterStoreFileName);
            if (!Files.exists(masterPath)) {
                noMaster(importFile);   //this happens when this is the first file being loaded
            } else {
                rewriteMaster(importFile);
            }
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * Writes import file to working and renames to master. Called when there isn't already a master file
     * @param importFile
     * @throws IOException
     * @throws Exception
     */
    private void noMaster(Map<ViewingKey, Viewing> importFile) throws IOException, Exception {
        Path workingFilePath = Paths.get(storePath.toString(), workingStoreFileName);
        if (Files.exists(workingFilePath)) Files.delete(workingFilePath);

        FileOutputStream outputStream = new FileOutputStream(workingFilePath.toString());
        for(Viewing viewing : importFile.values()) {
            outputStream.write(viewing.toBytes());
        }
        outputStream.close();

        Files.move(workingFilePath, Paths.get(storePath.toString(), masterStoreFileName));
    }

    /**
     * Rewrites master updating records when the import file contains a common key and appending left over import
     * records to end.
     *
     * @param importFile
     * @throws IOException
     * @throws Exception
     */
    private void rewriteMaster(Map<ViewingKey, Viewing> importFile) throws IOException, Exception {
        Path workingFilePath = Paths.get(storePath.toString(), workingStoreFileName);
        if (Files.exists(workingFilePath)) Files.delete(workingFilePath);

        Path masterFilePath = Paths.get(storePath.toString(), masterStoreFileName);

        FileOutputStream outputStream = new FileOutputStream(workingFilePath.toString());

        FileInputStream inputStream = new FileInputStream(masterFilePath.toString());
        byte[] inputBuffer = new byte[Viewing.BYTES];
        while(true) {
            int bytesRead = inputStream.read(inputBuffer, 0, Viewing.BYTES);

            //bytesRead should really only be -1 or Viewing.BYTES
            if (bytesRead == -1) break;
            else if (bytesRead < Viewing.BYTES) {
                //This condition implies the data store is corrupt. If the only affected record is the last then just
                //ignoring the problem saves all the good data. This is probably not a good assumption. I see
                //no good answer to this problem
                break;
            }

            Viewing masterCurrent = new Viewing(inputBuffer);
            ViewingKey masterCurrentKey = masterCurrent.getViewingKey();
            if (importFile.containsKey(masterCurrentKey)) {
                //remove matching viewing from import file and write it out to working
                Viewing importOverwrite = importFile.remove(masterCurrentKey);
                outputStream.write(importOverwrite.toBytes());
            }
            else {
                //no match in import file rewrite original record to working
                outputStream.write(masterCurrent.toBytes());
            }
        }
        inputStream.close();

        //anything left in the import file gets appended to end of working
        for(Viewing viewing : importFile.values()) {
            outputStream.write(viewing.toBytes());
        }
        outputStream.close();

        //Do file swap to make working master
        Path pendingPath = Paths.get(storePath.toString(), pendingStoreFileName);
        if (Files.exists(pendingPath)) Files.delete(pendingPath);
        Files.move(workingFilePath, pendingPath);
        Files.delete(masterFilePath);
        Files.move(pendingPath, masterFilePath);
    }


    /**
     * Provides access to the data in the store synchronized with updates.
     */
    public class Reader implements Closeable {

        private ReentrantLock lock;

        private InputStream inputStream;

        private Reader(DataStore store) throws FileNotFoundException {
            this.lock = store.lock;
            this.lock.lock();
            try {
                Path masterPath = Paths.get(storePath.toString(), masterStoreFileName);
                inputStream = new FileInputStream(masterPath.toString());
            }
            catch(Exception e) {
                lock.unlock();
                throw e;
            }
        }

        Viewing getNextViewing() throws IOException, Exception {
            byte[] inputBuffer = new byte[Viewing.BYTES];
            int bytesRead = inputStream.read(inputBuffer, 0, Viewing.BYTES);

            return bytesRead >= Viewing.BYTES ? new Viewing(inputBuffer) : null;
        }

        public void close()
        {
            try {
                inputStream.close();
            }
            catch(Exception e) {

            }
            finally {
                lock.unlock();
            }
        }
    }


}
