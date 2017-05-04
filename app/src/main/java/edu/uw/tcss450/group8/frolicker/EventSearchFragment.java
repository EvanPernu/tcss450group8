package groupn.tcss450.uw.edu.frolicker2;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import groupn.tcss450.uw.edu.frolicker2.Model.Event;

import static android.content.ContentValues.TAG;



/**
 * A simple {@link Fragment} subclass.
 */
public class EventSearchFragment extends Fragment {

    // EventBrite API key
    private static final String EVENTBRITE_KEY = "3E3LN6F6HUADRFXTS74Y";

    // search items
    private EditText et_eventSearch;
    private EditText et_locationSearch;
    private Button searchButton;

    public EventSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_search, container, false);

        et_eventSearch = (EditText) view.findViewById(R.id.editTextEvent);
        et_locationSearch = (EditText) view.findViewById(R.id.editTextLocation);
        searchButton = (Button) view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkSearchData(v);
            }
        });

        return view;
    }

    // selects the proper API request for the user search
    private void checkSearchData(View view) {

        String eventSearch = et_eventSearch.getText().toString();
        eventSearch = eventSearch.replace(' ', '+');

        String eventLocation = et_locationSearch.getText().toString();
        eventLocation = eventLocation.replace(' ', '+');

        Log.d(TAG, "checkSearchData: " + eventSearch.toString());
        if(eventSearch.equals("") && eventLocation.equals("")) {
            Log.d(TAG, "onClick: 1");
            new EventSearch().execute("https://www.eventbriteapi.com/v3/events/search/?token=" + EVENTBRITE_KEY + "&expand=venue");
        }else if(eventSearch.equals("") && !eventLocation.equals("")) {
            Log.d(TAG, "onClick: 2");
            new EventSearch().execute("https://www.eventbriteapi.com/v3/events/search/?location.address="
                    + eventLocation + "&token=" + EVENTBRITE_KEY + "&expand=venue");
        } else if(!eventSearch.equals("") && eventLocation.equals("")) {
            Log.d(TAG, "onClick: 3");
            new EventSearch().execute("https://www.eventbriteapi.com/v3/events/search/?q="
                    + eventSearch + "&token=" + EVENTBRITE_KEY + "&expand=venue");
        } else {
            Log.d(TAG, "onClick: 4");
            new EventSearch().execute("https://www.eventbriteapi.com/v3/events/search/?q="
                    + eventSearch + "&location.address=" + eventLocation + "&token=" + EVENTBRITE_KEY + "&expand=venue");
        }

    }

    // connects to API, parses JSON response, displays results in new fragment
    private class EventSearch extends AsyncTask<String, String, String> {

        private List<Event> eventList = new ArrayList<>();
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
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                //event = Event.getEvent(parentObject);

                JSONObject pageDataObject = parentObject.getJSONObject("pagination");
                int resultCount = pageDataObject.getInt("object_count");
                if(resultCount == 0) {
                    return "No events found";
                }
                JSONArray eventsArray = parentObject.getJSONArray("events");
                for(int i=0; i<eventsArray.length(); i++) {

                    Event event = new Event();

                    // parse event name
                    JSONObject eventObject = eventsArray.getJSONObject(i);
                    JSONObject eventNameObject = eventObject.getJSONObject("name");
                    event.setEventName(eventNameObject.getString("text"));

                    // parse location
                    JSONObject eventLocationObject = eventObject.getJSONObject("venue");
                    JSONObject address = eventLocationObject.getJSONObject("address");
                    event.setEventLocation(address.getString("city"));

                    // parse date
                    JSONObject dateObject = eventObject.getJSONObject("start");
                    event.setEventDate(dateObject.getString("local"));

                    // parse image if not null
                    if(!eventObject.get("logo_id").equals(null)) {
                        JSONObject eventImage = eventObject.getJSONObject("logo");
                        JSONObject imageObject = eventImage.getJSONObject("original");
                        event.setEventImage(imageObject.getString("url"));
                    }else{
                        event.setEventImage("null");
                    }
                    eventList.add(event);


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
                mainActivity.loadEventSearchResultFragment(eventList);
            }
        }
    }

}
