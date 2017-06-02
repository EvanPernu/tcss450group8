package edu.uw.tcss450.group8.frolicker.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import edu.uw.tcss450.group8.frolicker.MainActivity;
import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.EventSearchService;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * This fragment allows users to search for events with the following parameters:
 * <p>
 * Keyword
 * Distance
 *
 * @author Tim Weaver
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

    /**
     * Stores currently selected categories
     */
    private ArrayList<String> mCategories;

    /**
     * Instantiates a new Event search fragment.
     */
    public EventSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCategories = new ArrayList<String>();

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
        View view = inflater.inflate(R.layout.frag_test, container, false);

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
                hideSoftKeyBoard();
                searchInit();
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(orderSpinner.getVisibility() == View.VISIBLE) {
                    orderSpinner.setVisibility(View.GONE);
                    categoriesList.setVisibility(View.GONE);
                }else{
                    orderSpinner.setVisibility(View.VISIBLE);
                    categoriesList.setVisibility(View.VISIBLE);
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


        //initialize category checkboxes
        ArrayList<String> categoryOptions = new ArrayList<String>();

        for(String s : MainActivity.initCategories().keySet()){
            categoryOptions.add(s);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), R.layout.checklist_row,  categoryOptions);
        categoriesList.setAdapter(adapter);
        categoriesList.setItemsCanFocus(false);
        categoriesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctv = (CheckedTextView) view;
                if(ctv.isChecked()){
                    mCategories.add(ctv.getText().toString());
                }else{
                    mCategories.remove(ctv.getText().toString());
                }
            }
        });

        return view;
    }

     /**
      *  Selects the proper API request for the user search.
      */
    private void searchInit() {
        String event = etEventSearch.getText().toString();
        String location = etLocationSearch.getText().toString();
        String dialogMessage = "Finding events...";

        //if the user has not input any valid search criteria, show a message and return
        if(event.equals("") && location.equals("") && mCategories.isEmpty()){
            Toast.makeText(getContext(), "Enter event or location", Toast.LENGTH_LONG).show();
            return;
        }

        //build the search parameter URL based on criteria
        StringBuilder URL = new StringBuilder(EVENTBRITE_URL
                + "?token=" + EVENTBRITE_KEY + translateOrder(mOrder)
                + "&location.within="+mDistance+"mi"+"&expand=venue");

        if(!event.equals("")){
            URL.append("&q=");
            URL.append(event);
        }

        if(!mCategories.isEmpty()){
            URL.append("&categories=");
            ArrayList<Integer> idList = MainActivity.convertCategories(mCategories);

            for(int i = 0; i < idList.size(); i++){
                URL.append(idList.get(i));
                if(i != idList.size()-1){
                    URL.append(",");
                }
            }
        }

        if(location.equals("")){
            URL.append(mCurrentLocation);
        }else{
            URL.append("&location.address=");
            URL.append(location);
        }

        // begin search
        new EventSearchService(getContext(),dialogMessage).execute(URL.toString());


    }

    /**
     * A helper method to hide the keyboard from view when the user
     * clicks the search button.
     */
    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
