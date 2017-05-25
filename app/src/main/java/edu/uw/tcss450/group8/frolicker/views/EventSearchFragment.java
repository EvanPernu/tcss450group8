package edu.uw.tcss450.group8.frolicker.views;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import edu.uw.tcss450.group8.frolicker.MainActivity;
import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.EventCard;


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
                startEventSearch(v);
            }
        });

        return view;
    }

     /**
      *     selects the proper API request for the user search
      *
      *     @param view the parent view
      */
    private void startEventSearch(View view) {

        String eventSearch = etEventSearch.getText().toString();
        eventSearch = eventSearch.replace(' ', '+');

        String eventLocation = etLocationSearch.getText().toString();
        eventLocation = eventLocation.replace(' ', '+');

        if(eventSearch.equals("") && eventLocation.equals("")) {
            Toast.makeText(getContext(), "Enter event or location", Toast.LENGTH_LONG).show();
        }else if(eventSearch.equals("") && !eventLocation.equals("")) {
            new EventSearch().execute(EVENTBRITE_URL + "?location.address="
                    + eventLocation + "&token=" + EVENTBRITE_KEY + "&expand=venue");
        } else if(!eventSearch.equals("") && eventLocation.equals("")) {
            new EventSearch().execute(EVENTBRITE_URL + "?q="
                    + eventSearch + "&token=" + EVENTBRITE_KEY + "&expand=venue");
        } else {
            new EventSearch().execute(EVENTBRITE_URL + "?q="
                    + eventSearch + "&location.address=" + eventLocation + "&token=" + EVENTBRITE_KEY + "&expand=venue");
        }

    }

    /**
     *  connects to API, parses JSON response, displays results in new fragment
     */
    // connects to API, parses JSON response, displays results in new fragment
    private class EventSearch extends AsyncTask<String, String, String> {

        private List<EventCard> eventCardList = new ArrayList<>();
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext()) ;
            pDialog.setMessage("Finding events...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader((new InputStreamReader(stream)));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);

                JSONObject pageDataObject = parentObject.getJSONObject("pagination");
                int resultCount = pageDataObject.getInt("object_count");
                if(resultCount == 0) {
                    return "No events found";
                }
                JSONArray eventsArray = parentObject.getJSONArray("events");
                for(int i=0; i<eventsArray.length(); i++) {

                    //Event event = new Event();
                    EventCard eventCard = new EventCard();

                    JSONObject eventObject = eventsArray.getJSONObject(i);

                    // parse event name
                    JSONObject eventNameObject = eventObject.getJSONObject("name");
                    eventCard.setEventTitle(eventNameObject.getString("text"));

                    // parse city, address, long, and lat
                    JSONObject eventLocationObject = eventObject.getJSONObject("venue");
                    JSONObject location = eventLocationObject.getJSONObject("address");
                    eventCard.setEventCity(location.getString("city"));
                    eventCard.setEventLatitude(location.getString("latitude"));
                    eventCard.setEventLongitude(location.getString("longitude"));
                    eventCard.setEventStreetAddress(location.getString("address_1"));

                    // parse date and time
                    JSONObject dateObject = eventObject.getJSONObject("start");
                    eventCard.setEventStart(dateObject.getString("local"));
                    dateObject = eventObject.getJSONObject("end");
                    eventCard.setEventEnd(dateObject.getString("local"));

                    // parse description
                    JSONObject descriptionObject = eventObject.getJSONObject("description");
                    eventCard.setEventDescription(descriptionObject.getString("html"));

                    // parse image if not null
                    if(!eventObject.get("logo_id").equals(null)) {
                        JSONObject eventImage = eventObject.getJSONObject("logo");
                        JSONObject imageObject = eventImage.getJSONObject("original");
                        eventCard.setEventImgURL(imageObject.getString("url"));
                    }else{
                        eventCard.setEventImgURL("null");
                    }
                    eventCardList.add(eventCard);
                }
                return "";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if(s.equals("No events found")) {
                Toast.makeText(getContext(), "No events found", Toast.LENGTH_LONG).show();
            } else {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.loadEventSearchResultFragment(eventCardList);

            }
        }
    }

}
