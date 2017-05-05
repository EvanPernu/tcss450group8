package edu.uw.tcss450.group8.frolicker;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.PrefList;
import model.Event;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, PrefsInitFragment.OnFragmentInteractionListener{

    //The URL of the database
    private final String DB_URL = "http://cssgate.insttech.washington.edu/~_450agrp8/";

    //Tracks the username of the active user
    private String ACTIVE_USER = "Evan"; //change later
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create a new LoginFragment, load it
        if(savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new PrefsInitFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPrefsInitFragmentInteraction(String s, String theJSString) {
        if(s.equals("upload")){
            uploadInitPrefsTask task = new uploadInitPrefsTask();
            task.execute(ACTIVE_USER, theJSString);
        }
    }

    /**
     * AsyncTask to upload the initial user preferences to the database.
     * The first string passed in should be the username. The second
     * should be a string representation of the preference in JSONObject form.
     *
     * @author Evan Pernu
     * @author Charles Bryan (provided initial code)
     */
    private class uploadInitPrefsTask extends AsyncTask<String, Void, String> {
        private final String SERVICE = "PrefsInitUpload.php";
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 2) {
                throw new IllegalArgumentException("Two String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            try {
                URL urlObject = new URL(DB_URL + SERVICE + "?my_u="+strings[0]);
                Log.d("uploadPrefsInit", "URL = "+urlObject.toString());

                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());


                String data = URLEncoder.encode("my_prefs", "UTF-8")
                        + "=" + URLEncoder.encode(strings[1], "UTF-8");
                wr.write(data);
                wr.flush();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: "
                        + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }



        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
    }
    }

    
    // called when user clicks search button 
    public void loadEventSearchResultFragment(List<Event> eventList) {
        
        EventSearchResultFragment eventSearchResultFragment = new EventSearchResultFragment();
        eventSearchResultFragment.setEventList(eventList);
        eventSearchResultFragment.setRetainInstance(true);
       
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, eventSearchResultFragment
        ).addToBackStack(null).commit(); 
    }
}
