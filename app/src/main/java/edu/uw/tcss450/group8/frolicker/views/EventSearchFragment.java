package edu.uw.tcss450.group8.frolicker.views;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.EventSearchService;


/**
 * This fragment allows users to search for events with the following parameters:
 *
 * Keyword
 * Distance
 *
 * @author  Tim Weaver
 */
public class EventSearchFragment extends Fragment {

    // EvenBrite url
    private static final String EVENTBRITE_URL = "https://www.eventbriteapi.com/v3/events/search/";

    /**
     * EventBrite API key
     */
    private static final String EVENTBRITE_KEY = "3E3LN6F6HUADRFXTS74Y";

    /**
     * The field a user types keyword parameters into
     */
    private EditText etEventSearch;

    /**
     * The field a user types location parameters into
     */
    private EditText etLocationSearch;

    /**
     * Pressing this button commences a search
     */
    private Button searchButton;

    public EventSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_search, container, false);

        etEventSearch = (EditText) view.findViewById(R.id.editTextEvent);
        etLocationSearch = (EditText) view.findViewById(R.id.editTextLocation);
        searchButton = (Button) view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                searchInit();
            }
        });

        return view;
    }

     /**
      *     selects the proper API request for the user search
      */
    private void searchInit() {



        String event = etEventSearch.getText().toString();
        String location = etLocationSearch.getText().toString();
        String distance;
        String sortBy;
        String startDateKeyword;
        String dialogMessage = "Finding events...";



        switch(validateInput(event, location)) {

            case 0 :
                Toast.makeText(getContext(), "Enter event or location", Toast.LENGTH_LONG).show();
                break;
            case 1:
                new EventSearchService(getContext(),dialogMessage).execute(EVENTBRITE_URL + "?location.address="
                        + location + "&token=" + EVENTBRITE_KEY + "&expand=venue");
                break;
            case 2:
                new EventSearchService(getContext(),dialogMessage).execute(EVENTBRITE_URL + "?q="
                        + event + "&token=" + EVENTBRITE_KEY + "&expand=venue");
                break;
            case 3:
                new EventSearchService(getContext(),dialogMessage).execute(EVENTBRITE_URL + "?q="
                        + event + "&location.address=" + location + "&token="
                        + EVENTBRITE_KEY + "&expand=venue");
                break;
            default:
                break;
        }
    }

    private int validateInput(String event, String location) {

        if(event.equals("") && location.equals("")) {
            return 0;
        }else if(event.equals("") && !location.equals("")) {
            return 1;
        } else if(!event.equals("") && location.equals("")) {
            return 2;
        } else {
            return 3;
        }
    }

}
