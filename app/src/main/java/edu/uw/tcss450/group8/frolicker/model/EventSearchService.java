package edu.uw.tcss450.group8.frolicker.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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


/**
 * The EventSearchService class is an AsyncTask for searching for events.
 *
 * @author Tim Weaver
 */
public class EventSearchService extends AsyncTask<String, String, String> {

    private List<EventCard> eventCardList = new ArrayList<>();
    private ProgressDialog pDialog;
    private String dialogMessage;
    private Context context;


    /**
     * Instantiates a new Event search service.
     *
     * @param context       the context
     * @param dialogMessage the dialog message
     */
    public EventSearchService(Context context, String dialogMessage) {
        this.context = context;
        this.dialogMessage = dialogMessage;
    }

    /**
     * Initializes a progress dialog.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(dialogMessage);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    /**
     * Retrieves and parses event data from EventBrite API
     *
     * @param params
     * @return
     */
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
            if (resultCount == 0) {
                return "No events found";
            }
            JSONArray eventsArray = parentObject.getJSONArray("events");
            for (int i = 0; i < eventsArray.length(); i++) {


                EventCard eventCard = new EventCard();
                JSONObject eventObject = eventsArray.getJSONObject(i);

                // parse event name
                eventCard.setEventName(eventObject.getJSONObject("name").getString("text"));

                // parse city, address, long, and lat
                eventCard.setEventVenue(eventObject.getJSONObject("venue").getString("name"));
                JSONObject location = eventObject.getJSONObject("venue")
                        .getJSONObject("address");
                eventCard.setEventCity(location.getString("city"));
                eventCard.setEventLatitude(location.getString("latitude"));
                eventCard.setEventLongitude(location.getString("longitude"));
                eventCard.setEventStreetAddress(location.getString("address_1"));

                // parse date and time
                eventCard.setEventStart(eventObject.getJSONObject("start").getString("local"));
                eventCard.setEventEnd(eventObject.getJSONObject("end").getString("local"));

                // parse category
                eventCard.setEventCategoryId(eventObject.getString("category_id"));

                // parse description
                eventCard.setEventDescription(eventObject.getJSONObject("description")
                        .getString("html"));

                // parse image if not null
                if (!eventObject.get("logo_id").equals(null)) {
                    eventCard.setEventImgURL(eventObject.getJSONObject("logo")
                            .getJSONObject("original")
                            .getString("url"));
                } else {
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
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Loads a new fragment to display search results
     *
     * @param s the status of the search
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (s != null && s.equals("No events found")) {
            Toast.makeText(context.getApplicationContext(), "No events found", Toast.LENGTH_LONG).show();

        } else {
            MainActivity mainActivity = (MainActivity)context;
            mainActivity.loadNextEventFragment(eventCardList,1);
        }
    }
}
