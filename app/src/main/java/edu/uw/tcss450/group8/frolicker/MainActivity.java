package edu.uw.tcss450.group8.frolicker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.group8.frolicker.model.EventCard;
import edu.uw.tcss450.group8.frolicker.views.EventCardRecycler;
import edu.uw.tcss450.group8.frolicker.views.EventSearchFragment;
import edu.uw.tcss450.group8.frolicker.views.HomeFragment;
import edu.uw.tcss450.group8.frolicker.views.LoginFragment;
import edu.uw.tcss450.group8.frolicker.views.LoginOrRegisterFragment;
import edu.uw.tcss450.group8.frolicker.views.PrefsInitFragment;
import edu.uw.tcss450.group8.frolicker.views.RegisterFragment;

/**
 * The main activity that controls internal data, fragment display, click listeners, and AsyncTasks
 *
 * @author Evan Pernu
 * @author Chris Dale
 * @author Tim Weaver
 */
public class MainActivity extends AppCompatActivity
        implements
        LoginOrRegisterFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        PrefsInitFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener {


    // EvenBrite url
    private static final String EVENTBRITE_URL = "https://www.eventbriteapi.com/v3/events/search/";

    /**
     * EventBrite API key
     */
    private static final String EVENTBRITE_KEY = "3E3LN6F6HUADRFXTS74Y";

    //The URL of the database
    private final String DB_URL = "http://cssgate.insttech.washington.edu/~_450agrp8/";

    //Tracks the username of the active user
    private String ACTIVE_USER;

    //The URL of login
    private static final String PARTIAL_LOGIN_URL = "http://cssgate.insttech.washington.edu/"
            + "~_450agrp8/feedback";

    //The user's home fragment
    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        

        //create a new LoginFragment, load it
        if(savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new LoginOrRegisterFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onPrefsInitFragmentInteraction(String s, String theJSString) {
        if(s.equals("upload")){
            //upload their prefs to the server
            uploadInitPrefsTask task = new uploadInitPrefsTask();
            task.execute(ACTIVE_USER, theJSString);

            Log.d("main/upload", "about to log in");

            //log them in automatically
            FragmentTransaction loginTransaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new EventSearchFragment())
                    .addToBackStack(null);
            loginTransaction.commit();
        }
    }

    @Override
    public void onHomeFragmentInteraction(int n) {
        switch(n) {
            case 1:
                FragmentTransaction secondTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new EventSearchFragment())
                        .addToBackStack(null);
                secondTransaction.commit();
                break;

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


    /**
     *  called when user clicks search button
     *
     *  @param eventCardList a list of each event in card form
     *
     *  @author Tim Weaver
     */
    public void loadEventSearchResultFragment(List<EventCard> eventCardList) {

        EventCardRecycler eventCardRecycler = new EventCardRecycler();
        eventCardRecycler.setEventCardList(eventCardList);
        eventCardRecycler.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, eventCardRecycler
        ).addToBackStack(null).commit();

    }



    /**
     * Does different things based on the fragment we want to go to.
     * 2: Login
     * 3: Register
     * 4: Attempting login
     * 5: Attempting register
     * @param theFrag
     * @author Chris Dale
     */
    @Override
    public void onFragmentInteraction(int theFrag) {

        AsyncTask<String, Void, String> task = null;
        String message = null;

        switch (theFrag) {

            case 2:


                LoginFragment loginFragment = new LoginFragment();
                FragmentTransaction secondTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, loginFragment)
                        .addToBackStack(null);
                secondTransaction.commit();
                break;
            case 3:
                RegisterFragment registerFragment = new RegisterFragment();

                FragmentTransaction thirdTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, registerFragment)
                        .addToBackStack(null);
                thirdTransaction.commit();
                break;
            case 4:
                // quick login

                EditText editUsername = (EditText) findViewById(R.id.editUsername);
                EditText editPassword = (EditText) findViewById(R.id.editPassword);

                String usernameString = editUsername.getText().toString();
                String passwordString = editPassword.getText().toString();

                if (passwordString.equals("") || usernameString.equals(""))
                {
                    // Warn the user.
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("You didn't fill in a box!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Dismiss box...
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {
                    Log.d("onFragmentInteraction", "Attempting Login");

                    task = new LoginWebServiceTask();
                    task.execute(PARTIAL_LOGIN_URL, usernameString, passwordString);
                }
                break;
            case 5:
                EditText editRegisUsername = (EditText) findViewById(R.id.editUsername);
                EditText editRegisPassword = (EditText) findViewById(R.id.editPassword);
                EditText editRegisConfirm = (EditText) findViewById(R.id.editConfirm);

                String usernameRegisString = editRegisUsername.getText().toString();
                String passwordRegisString = editRegisPassword.getText().toString();
                String confirmRegisString = editRegisConfirm.getText().toString();

                if (       passwordRegisString.equals("")
                        || usernameRegisString.equals("")
                        || confirmRegisString.equals(""))
                {
                    // Warn the user.
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("You didn't fill in a box!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Dismiss box...
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else if (!passwordRegisString.equals(confirmRegisString))
                {
                    // Warn the user.
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("Your passwords dont match!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Dismiss box...
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else if (passwordRegisString.length() < 8)
                {
                    // Warn the user.
                    new AlertDialog.Builder(this)
                            .setTitle("Warning")
                            .setMessage("Your password must be at least 8 characters long!")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Dismiss box...
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {
                    task = new RegisterWebServiceTask();
                    task.execute(PARTIAL_LOGIN_URL, usernameRegisString, passwordRegisString);
                }
                break;
        }
    }

    /**
     * Asynctask for register
     *
     * @author Chris Dale
     * @author Evan Pernu
     */
    private class RegisterWebServiceTask extends AsyncTask<String, Void, String> {
        private final String SERVICE = "_register.php";
        private String mUsername;

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }

            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];

            //set the active username it knows who to log in later
            mUsername = strings[1];

            try {
                URL urlObject = new URL(url + SERVICE);
                urlConnection = (HttpURLConnection) urlObject.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                String data = URLEncoder.encode("new_username", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("new_password", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");

                wr.write(data);
                wr.flush();

                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: " + e.getMessage();

            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }

            Log.d("doInBackground", "Finished.");

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }

            if(result.equals("success")){
                //successful registration
                Toast.makeText(getApplicationContext(), "Registration success!", Toast.LENGTH_LONG)
                        .show();

                //log the user in automatically
                //set active user
                ACTIVE_USER = mUsername;

                //take the user to PrefsInitFragment
                FragmentTransaction loginTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new PrefsInitFragment())
                        .addToBackStack(null);

                // Commit the transaction
                loginTransaction.commit();

            }else if(result.equals("fail")){
                //unsuccessful registration for other (unknown/error) reasons
                Toast.makeText(getApplicationContext(), "Registration failed. There was an error.", Toast.LENGTH_LONG)
                        .show();
            }else if(result.equals("taken")){
                //unsuccessful registration, username is taken
                Toast.makeText(getApplicationContext(), "This username is already taken :(", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    /**
     * Asynctask for login
     *
     * @author Chris Dale
     * @author Evan Pernu
     */
    private class LoginWebServiceTask extends AsyncTask<String, Void, String> {
        private final String SERVICE = "_login.php";
        private String mUsername;

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }

            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];

            //set the active username it knows who to log in later
            mUsername = strings[1];

            try {
                URL urlObject = new URL(url + SERVICE);
                urlConnection = (HttpURLConnection) urlObject.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8");
                data += "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(strings[2], "UTF-8");
                wr.write(data);

                wr.flush();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: " + e.getMessage();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }

            Log.d("doInBackground", "Finished. reponse = "+response);


            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }

            //Log.d("onPostExecute", "Making next fragment");

            if(result.equals("success")){
                //set active user
                ACTIVE_USER = mUsername;

                //successful login
                Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_LONG)
                        .show();

                mHomeFragment = new HomeFragment();

                //---------------------------------------------TODO Change to user's current location-----------------------------------------------------------------------------------------------------
                String location = "Seattle";
                String event = "Music";

                new EventSearch().execute(EVENTBRITE_URL + "?q="
                        + event + "&location.address=" + location + "&token="
                        + EVENTBRITE_KEY + "&expand=venue");

                FragmentTransaction loginTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer,  mHomeFragment)
                        .addToBackStack(null);
                loginTransaction.commit();

            }else if(result.equals("fail")){
                //unsuccessful login
                Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }


    /**
     *  connects to API, parses JSON response, displays results in new fragment
     *
     *  @author Tim Weaver
     */
    private class EventSearch extends AsyncTask<String, String, String> {

        private List<EventCard> eventCardList = new ArrayList<>();
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this) ; //TODO different getcontext() call?------------------------------------------------------------------------------------------
            pDialog.setMessage("Connecting to Server");
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


                    EventCard eventCard = new EventCard();
                    JSONObject eventObject = eventsArray.getJSONObject(i);

                    // parse event name
                    eventCard.setEventName(eventObject.getJSONObject("name").getString("text"));

                    // parse city, address, long, and lat
                    JSONObject location = eventObject.getJSONObject("venue")
                            .getJSONObject("address");
                    eventCard.setEventCity(location.getString("city"));
                    eventCard.setEventLatitude(location.getString("latitude"));
                    eventCard.setEventLongitude(location.getString("longitude"));
                    eventCard.setEventStreetAddress(location.getString("address_1"));

                    // parse date and time
                    eventCard.setEventStart(eventObject.getJSONObject("start").getString("local"));
                    eventCard.setEventEnd(eventObject.getJSONObject("end").getString("local"));

                    // parse description
                    eventCard.setEventDescription(eventObject.getJSONObject("description")
                            .getString("html"));

                    // parse image if not null
                    if(!eventObject.get("logo_id").equals(null)) {
                        eventCard.setEventImgURL(eventObject.getJSONObject("logo")
                                .getJSONObject("original")
                                .getString("url"));
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
                Toast.makeText(getApplicationContext(), "No events found", Toast.LENGTH_LONG).show();
            } else {
                Log.d("homeAsync", "about to etEventCardList(");
                mHomeFragment.setEventCardList(eventCardList);
            }
        }
    }
}
