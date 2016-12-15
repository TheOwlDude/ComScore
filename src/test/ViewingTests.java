
import org.junit.*;

import java.util.Date;

/**
 * Created by Brian on 12/15/2016.
 */
public class ViewingTests {

    @Test
    public void roundTripBinarySerialization() throws Exception {

        Viewing viewing = new Viewing("stb1|the matrix|warner bros|2014-04-01|4.00|1:30");
        byte[] viewingbytes = viewing.toBytes();
        Viewing fromBytes = new Viewing(viewingbytes);
        Assert.assertEquals("stb1", fromBytes.setTopBoxId);
        Assert.assertEquals("the matrix", fromBytes.title);
        Assert.assertEquals("warner bros", fromBytes.provider);
        Assert.assertEquals(new Date(114, 3, 1), fromBytes.viewDate); //no wonder this is deprecated
        Assert.assertEquals(400, fromBytes.revenueInCents);
        Assert.assertEquals(90, fromBytes.viewDurationInMinutes);
    }
}
