

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by bkcol_000 on 12/14/2016.
 *
 * Represents an interval of media viewing
 */
public class Viewing {
    public String setTopBoxId;
    public String title;
    public String provider;
    public Date viewDate;
    public long revenueInCents;          //no reason to restrict revenue by making this an int :-)
    public int viewDurationInMinutes;

    /**
     * The fixed size of the binary representation of a Viewing
     */
    public static final int BYTES = 3 * 64 + 10 + Long.BYTES + Integer.BYTES;


    public Viewing(String importFileRecord) throws Exception {
        String[] components = importFileRecord.split(Pattern.quote("|"));
        for(int i = 0; i < components.length; ++i) {
            components[i] = components[i].trim();
        }

        if (components == null) throw new Exception("components is required");
        if (components.length != 6) throw new Exception(String.format("Unexpected number of components: %s", components.length));

        if (components[0] == null || components[0].equals("")) throw new Exception("setTopBoxId is required.");
        setTopBoxId = components[0];

        if (components[1] == null || components[1].equals("")) throw new Exception("title is required.");
        title = components[1];

        if (components[2] == null || components[2].equals("")) throw new Exception("provider is required.");
        provider = components[2];

        if (components[3] == null || components[3].equals("")) throw new Exception("view date is required.");
        DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            viewDate = sourceFormat.parse(components[3]);
        }
        catch(Exception e) {
            throw new Exception(String.format("Failed to parse view date %s in format yyyy-MM-dd", components[3]), e);
        }

        if (components[4] == null || components[4].equals("")) throw new Exception("revenue is required.");
        try {
            int dotPosition = components[4].indexOf('.');
            int dollars = Integer.parseInt(components[4].substring(0, dotPosition));
            if (dollars < 0) throw new Exception("dollars can't be negative");
            int cents = Integer.parseInt(components[4].substring(dotPosition + 1));
            if (cents < 0 || cents > 99) throw new Exception("cents must be within [0,99]");
            revenueInCents = 100 * dollars + cents;
        }
        catch(Exception e) {
            throw new Exception(String.format("Failed to parse %s as an amount of money.", components[4]), e);
        }

        if (components[5] == null || components[5].equals("")) throw new Exception("viewing time is required.");
        try {
            int colonPosition = components[5].indexOf(':');
            int hours = Integer.parseInt(components[5].substring(0, colonPosition));
            if (hours < 0) throw new Exception("hours can't be negative");
            int minutes = Integer.parseInt(components[5].substring(colonPosition + 1));
            if (minutes < 0 || minutes > 59) throw new Exception("minutes must be within [0,59]");
            viewDurationInMinutes = 60 * hours + minutes;
        }
        catch(Exception e) {
            throw new Exception(String.format("Failed to parse %s as a duration.", components[5]), e);
        }
    }


    /**
     * Creates a main.Brian.Cole.ComScore.Viewing from the byte[] format found in the DataStore
     * @param bytes
     * @throws Exception
     */
    public Viewing(byte[] bytes) throws Exception {
        if (bytes.length != BYTES) throw new Exception("bytes argument has incorrect length");

        setTopBoxId = new String(bytes, 0, 64, StandardCharsets.UTF_8).trim();
        title = new String(bytes, 64, 64, StandardCharsets.UTF_8).trim();
        provider = new String(bytes, 128, 64, StandardCharsets.UTF_8).trim();
        String dateString = new String(bytes, 192, 10, StandardCharsets.UTF_8);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        viewDate = sdf.parse(dateString);
        ByteBuffer revenueBuffer = ByteBuffer.allocate(Long.BYTES);
        revenueBuffer.put(bytes, 202, Long.BYTES);
        revenueBuffer.flip();
        revenueInCents = revenueBuffer.getLong();
        ByteBuffer durationBuffer = ByteBuffer.allocate(Integer.BYTES);
        durationBuffer.put(bytes, 202 + Long.BYTES, Integer.BYTES);
        durationBuffer.flip();
        viewDurationInMinutes = durationBuffer.getInt();
    }

    /**
     * Returns the fixed width byte[] that will be written to the data store for this main.Brian.Cole.ComScore.Viewing
     *
     * @return
     */
    public byte[] toBytes() throws Exception {

        byte[] bytes = new byte[BYTES];
        System.arraycopy(spacePadding, 0, bytes, 0, spacePadding.length);

        //for the purpose of this exercise everything is low ASCII. 64 character strings always take 64 bytes.
        byte[] idBytes = setTopBoxId.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(idBytes, 0, bytes, 0, idBytes.length);

        byte[] titleBytes = title.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(titleBytes, 0, bytes, 64, titleBytes.length);

        byte[] providerBytes = provider.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(providerBytes, 0, bytes, 128, provider.length());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String viewDateString = sdf.format(viewDate);
        byte[] viewDateBytes = viewDateString.getBytes(StandardCharsets.UTF_8);
        if (viewDateBytes.length != 10) throw new Exception("Error formatting view date");
        System.arraycopy(viewDateBytes, 0, bytes, 192, viewDateBytes.length);

        ByteBuffer revenueBuffer = ByteBuffer.allocate(Long.BYTES);
        revenueBuffer.putLong(revenueInCents);
        System.arraycopy(revenueBuffer.array(), 0, bytes, 202, Long.BYTES);

        ByteBuffer durationBuffer = ByteBuffer.allocate((Integer.BYTES));
        durationBuffer.putInt(viewDurationInMinutes);
        System.arraycopy(durationBuffer.array(), 0, bytes, 202 + Long.BYTES, Integer.BYTES);

        return bytes;
    }


    public ViewingKey getViewingKey() { return new ViewingKey(this); }


    public String getStringValueForQueryField(QueryField queryField) throws Exception {
        switch(queryField) {
            case STB: return setTopBoxId;
            case TITLE: return title;
            case PROVIDER: return provider;
            case DATE:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(viewDate);
            case REV: return String.format("%d.%02d", revenueInCents / 100, revenueInCents % 100);
            case VIEW_TIME: return String.format("%d:%02d", viewDurationInMinutes / 60, viewDurationInMinutes % 60);
            default: throw new Exception (String.format("Unexeoected queryFiled: %s", queryField));
        }
    }


    private static byte[] spacePadding;

    /**
     * Storing all the fields fixed width in the data store. Padding strings with spaces so that can use Trim() to
     * easily cut the string to its actual length. Not sure this is tremendously efficient but it is simple.
     */
    static {
        spacePadding = new byte[64 * 3];
        for(int i = 0; i < 64 * 3; ++i) spacePadding[i] = 32;
    }
}

