package groupn.tcss450.uw.edu.frolicker2.Model;



import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Event extends Fragment {

    private String eventName;
    private String eventLocation;
    private String eventDate;
    private String eventImage;
    private String eventPrice;


    public Event() {
        // Required empty public constructor
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventPrice() {
        return eventPrice;
    }

    public void setEventPrice(String eventPrice) {
        this.eventPrice = eventPrice;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    
}
