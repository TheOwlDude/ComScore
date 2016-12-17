import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Brian on 12/15/2016.
 *
 * Represents the unique id for a store item i.e. stb, title, and date
 *
 * Can be used as a key in a map to determine when an item is duplicated and needs to be updated rather than inserted
 *
 */
public class ViewingKey {
    private String setTopBoxId;
    private String title;
    private String date;

    public ViewingKey(Viewing viewing) {
        this.setTopBoxId = viewing.setTopBoxId;
        this.title = viewing.title;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.date =  sdf.format(viewing.viewDate);
    }

    @Override
    public boolean equals(Object other) {
        if((other == null) || (getClass() != other.getClass())) return false;

        ViewingKey otherAsViewingKey = (ViewingKey)other;
        return
            this.setTopBoxId.equals(otherAsViewingKey.setTopBoxId) &&
            this.title.equals(otherAsViewingKey.title) &&
            this.date.equals(otherAsViewingKey.date);
    }

    @Override
    public int hashCode() {
        return setTopBoxId.hashCode() ^ title.hashCode() ^ date.hashCode();
    }
}
