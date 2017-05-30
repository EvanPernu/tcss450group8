package edu.uw.tcss450.group8.frolicker.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.uw.tcss450.group8.frolicker.MainActivity;
import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.EventSearchService;


/**
 * This fragment allows users to search for events with the following parameters:
 *
 * Keyword
 * Distance
 *
 * @author  Tim Weaver
 * @author Evan Pernu
 */
public class EventSearchFragment extends Fragment {

    // EvenBrite url
    private static final String EVENTBRITE_URL = "https://www.eventbriteapi.com/v3/events/search/";

    /**
     * EventBrite API key
     */
    private static final String EVENTBRITE_KEY = "3E3LN6F6HUADRFXTS74Y";

    /**
     * minimum search distance in miles
     */
    private static final int MIN_SEARCH_DISTANCE = 1;

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

    /**
     * This bar determines the search distance
     */
    private SeekBar distanceBar;

    /**
     * Displays search distance to user
     */
    private TextView distanceTxt;

    /**
     * Displays a list of sorting options
     */
    private Spinner orderSpinner;

    /**
     * Displays a list of categories to search by
     */
    private ListView categoriesList;

    /**
     * Expands search options
     */
    private Button expandButton;

    /**
     * The search distance in miles.
     * default = 10.
     * this value needs to match fragment_event_search.xml -> barSearchDistance -> android:progress
     */
    private int mDistance = 10;

    /**
     * This determines the order results are displayed in
     */
    private String mOrder = "Date(soonest)";

    /**
     * This stores the user's current location in EventBrite URL format
     */
    private String mCurrentLocation = "";

    public EventSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = this.getArguments();
        if (args != null) {
            mCurrentLocation=args.getString("loc");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_search, container, false);
        //create references to UI elementstainer, false);

        etEventSearch = (EditText) view.findViewById(R.id.editTextEvent);
        etLocationSearch = (EditText) view.findViewById(R.id.editTextLocation);
        searchButton = (Button) view.findViewById(R.id.searchButton);
        distanceBar = (SeekBar) view.findViewById(R.id.barSearchDistance);
        distanceTxt = (TextView) view.findViewById(R.id.txtSearchDistance);
        orderSpinner = (Spinner) view.findViewById(R.id.spinnerOrder);
        categoriesList = (ListView) view.findViewById(R.id.listCategories);
        expandButton = (Button) view.findViewById(R.id.btnSearchOptions);

        //initialize UI elements
        distanceTxt.setText("Within "+mDistance+" miles");
        final ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.search_order_options_array, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(spinAdapter);


        //hide expanded options
        etLocationSearch.setVisibility(View.GONE);
        orderSpinner.setVisibility(View.GONE);
        categoriesList.setVisibility(View.GONE);

        //set listeners
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                int distance;
                                                   @Override
                                                   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                       distance = progress + MIN_SEARCH_DISTANCE;
                                                        distanceTxt.setText("Within "+distance+" miles");
                                                   }

                                                   @Override
                                                   public void onStartTrackingTouch(SeekBar seekBar) {

                                                   }

                                                   @Override
                                                   public void onStopTrackingTouch(SeekBar seekBar) {
                                                        mDistance = distance;
                                                   }
                                               });

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                searchInit();
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(etLocationSearch.getVisibility() == View.GONE){
                    //if the expanded options are hidden, show them
                    etLocationSearch.setVisibility(View.VISIBLE);
                    orderSpinner.setVisibility(View.VISIBLE);
                    categoriesList.setVisibility(View.VISIBLE);

                    //TODO change drawable toe xpand/collapse
                    //expandButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable., 0, 0, 0);

                }else{
                    //if the expanded options are shown, reset and collapse them
                    etLocationSearch.setVisibility(View.GONE);
                    orderSpinner.setVisibility(View.GONE);
                    categoriesList.setVisibility(View.GONE);

                    etLocationSearch.setText(null);
                    //TODO set spinner to default option, uncheck all categories
                }
            }
        });

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String[] options = getResources().getStringArray(R.array.search_order_options_array);
                mOrder = options[parentView.getSelectedItemPosition()];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //this should not be called
            }

        });


        ArrayList<String> categoryOptions = new ArrayList<String>();

        for(String s : MainActivity.initCategories().keySet()){
            categoryOptions.add(s);
        }

        // set adapter for listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.checklist_row, R.id.txtCategoryList, categoryOptions);
        categoriesList.setAdapter(adapter);
        categoriesList.setItemsCanFocus(false);
        // we want multiple clicks
        categoriesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);





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

            //there is no user input
            case 0 :
                Toast.makeText(getContext(), "Enter event or location", Toast.LENGTH_LONG).show();
                break;
            case 1:
                new EventSearchService(getContext(),dialogMessage).execute(EVENTBRITE_URL + "?location.address="
                        + location + "&token=" + EVENTBRITE_KEY + "&expand=venue"
                        +mCurrentLocation+translateOrder(mOrder)+"&location.within="+mDistance+"mi");
                break;
            case 2:
                new EventSearchService(getContext(),dialogMessage).execute(EVENTBRITE_URL + "?q="
                        + event + "&token=" + EVENTBRITE_KEY + "&expand=venue"
                        +mCurrentLocation+translateOrder(mOrder)+"&location.within="+mDistance+"mi");
                break;
            case 3:
                new EventSearchService(getContext(),dialogMessage).execute(EVENTBRITE_URL + "?q="
                        + event + "&location.address=" + location + "&token="
                        + EVENTBRITE_KEY + "&expand=venue"
                        +translateOrder(mOrder)+"&location.within="+mDistance+"mi");
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

    /**
     * Private helper method that translates the user's order selection into EventBrite URL language
     * @param input the user order selection
     * @return the corresponding EventBrite URL term
     */
    private String translateOrder(String input){
        String result;

        if(input.equals("Date (soonest)")){
            result = "date";
        }else if(input.equals("Date (latest)")){
            result = "-date";
        }else if(input.equals("Distance (near to far)")){
            result = "distance";
        }else if(input.equals("Distance (far to near)")){
            result = "-distance";
        }else{
            result = "best";
        }

        return "&sort_by="+result;
    }
}
