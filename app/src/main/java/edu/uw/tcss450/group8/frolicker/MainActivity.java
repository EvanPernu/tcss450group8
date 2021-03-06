package edu.uw.tcss450.group8.frolicker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.group8.frolicker.model.EventCard;
import edu.uw.tcss450.group8.frolicker.model.EventSearchService;
import edu.uw.tcss450.group8.frolicker.model.PrefList;
import edu.uw.tcss450.group8.frolicker.views.EventMapFragment;
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
        HomeFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //eventbrite api information
    private static final String EVENTBRITE_URL = "https://www.eventbriteapi.com/v3/events/search/";
    private static final String EVENTBRITE_KEY = "3E3LN6F6HUADRFXTS74Y";


    //name of active user
    private String ACTIVE_USER;

    //location of web service scripts
    //login
    private static final String PARTIAL_LOGIN_URL = "http://cssgate.insttech.washington.edu/"
            + "~_450agrp8/feedback";
    //database
    private final String DB_URL = "http://cssgate.insttech.washington.edu/~_450agrp8/";

    //google api information
    private GoogleApiClient mGoogleApiClient;
    private static final String DEFUALT_SEARCH_RADIUS = "10mi";
    private static final String PREFS_NAME = "REMEMBERED_USER";
    private static String GET_USERNAME = "SAVED_USER";
    private static String GET_PASSWORD = "SAVED_PASSWORD";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int MY_PERMISSIONS_LOCATIONS = 814;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Context context = this;

    //stores the active user's event prefs in JSON format
    private String mEventPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setupLocation();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = new LocationRequest();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_LOCATIONS);
        }

        //create a new LoginFragment, load it
        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new LoginOrRegisterFragment())
                        .commit();
            }
        }

        mEventPrefs = "";
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mCurrentLocation == null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mCurrentLocation != null) Log.i("MAINONCONNECTED", mCurrentLocation.toString());
                            startLocationUpdates();
                        }
                    }
                } else {
                    // permission denied
                    Toast.makeText(this,
                            "Locations need to be working for this portion, please provide permission",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                Log.d("h", "onConnected: butt");
                if (mCurrentLocation != null) Log.i("MAINONCONNECTED", mCurrentLocation.toString());
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onPrefsInitFragmentInteraction(String s, String theJSString) {
        if (s.equals("upload")) {
            //upload their prefs to the server
            uploadInitPrefsTask task = new uploadInitPrefsTask();
            task.execute(ACTIVE_USER, theJSString);

            // automatic search for events near current location when logging in
            new EventSearchService(context, "Finding events...").execute(EVENTBRITE_URL + "?location.latitude="
                    + String.valueOf(mCurrentLocation.getLatitude()) + "&location.longitude="
                    + String.valueOf(mCurrentLocation.getLongitude()) + "&token="
                    + EVENTBRITE_KEY + "&expand=venue");
        }
    }

    @Override
    public void onHomeFragmentInteraction(int n) {
        switch (n) {
            case 1:
                EventSearchFragment mEventSearchFragment = new EventSearchFragment();
                mEventSearchFragment.setRetainInstance(true);
                Bundle bundle = new Bundle();
                bundle.putString("loc", getCurrentLocation());
                mEventSearchFragment.setArguments(bundle);

                FragmentTransaction secondTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, mEventSearchFragment)
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
                URL urlObject = new URL(DB_URL + SERVICE + "?my_u=" + strings[0]);

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
     * Loads a new fragment based on user actions.
     *
     * 0: Event search
     * 1: Home fragment
     * 2: Map fragment
     *
     * @param eventCardList a list of each event in card form
     * @author Tim Weaver
     */
    // loads fragments that require event data
    public void loadNextEventFragment(List<EventCard> eventCardList, int frag) {

        switch (frag) {

            case 0:
                EventSearchFragment mEventSearchFragment = new EventSearchFragment();
                mEventSearchFragment.setRetainInstance(true);
                Bundle bundle = new Bundle();
                bundle.putString("loc", getCurrentLocation());
                mEventSearchFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mEventSearchFragment
                ).addToBackStack(null).commit();
                break;

            case 1:
                HomeFragment mHomeFragment = new HomeFragment();
                Bundle args = new Bundle();
                args.putString("name", ACTIVE_USER);
                mHomeFragment.setArguments(args);
                mHomeFragment.setEventCardList(eventCardList);
                mHomeFragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mHomeFragment
                ).addToBackStack(null).commit();
                break;

            case 2:
                EventMapFragment mapFragment = new EventMapFragment();
                mapFragment.setEventCardList(eventCardList);
                mapFragment.setRetainInstance(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mapFragment)
                        .addToBackStack(null).commit();
                break;

            default:
                break;
        }
    }

    /**
     * Does different things based on the fragment we want to go to.
     * 2: Login
     * 3: Register
     * 4: Attempting login
     * 5: Attempting register
     * 6: Logout
     * @param theFrag
     * @author Chris Dale
     */
    @Override
    public void onFragmentInteraction(int theFrag) {

        AsyncTask<String, Void, String> task = null;
        String message = null;

        switch (theFrag) {

            case 2:
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                if(settings.contains(GET_USERNAME) && settings.contains(GET_PASSWORD)){
                    //if we have a username and pw saved, don't load the fragment, just go
                    task = new LoginWebServiceTask();
                    task.execute(PARTIAL_LOGIN_URL, settings.getString(GET_USERNAME, ""), settings.getString(GET_PASSWORD, ""));
                }

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
                Log.d("g", "onFragmentInteraction: dick" + mCurrentLocation);
                EditText editUsername = (EditText) findViewById(R.id.editUsername);
                EditText editPassword = (EditText) findViewById(R.id.editPassword);
                CheckBox remember = (CheckBox) findViewById(R.id.rememberCheck);
                boolean doRemember = remember.isChecked();
                String usernameString = editUsername.getText().toString();
                String passwordString = editPassword.getText().toString();

                if (passwordString.equals("") || usernameString.equals("")) {
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
                } else {
                    Log.d("onFragmentInteraction", "Attempting Login");
                    Log.d("t", "onFragmentInteraction: ass" + mCurrentLocation);
                    if(doRemember){ //Save the stuff
                        SharedPreferences sharedSettings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedSettings.edit();
                        //put in new stuff
                        editor.putString(GET_USERNAME, usernameString);
                        editor.putString(GET_PASSWORD, passwordString);
                        editor.commit();
                    }
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

                if (passwordRegisString.equals("")
                        || usernameRegisString.equals("")
                        || confirmRegisString.equals("")) {
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
                } else if (!passwordRegisString.equals(confirmRegisString)) {
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
                } else if (passwordRegisString.length() < 8) {
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
                } else {
                    task = new RegisterWebServiceTask();
                    task.execute(PARTIAL_LOGIN_URL, usernameRegisString, passwordRegisString);
                }
                break;
            case 6:
                SharedPreferences sharedSettings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedSettings.edit();
                //clear the login details
                editor.remove(GET_USERNAME);
                editor.remove(GET_PASSWORD);
                editor.commit();

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new LoginOrRegisterFragment())
                        .commit();
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
        private ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Registering...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

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

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }


            if (result.equals("success")) {
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

            } else if (result.equals("fail")) {
                //unsuccessful registration for other (unknown/error) reasons
                Toast.makeText(getApplicationContext(), "Registration failed. There was an error.", Toast.LENGTH_LONG)
                        .show();
            } else if (result.equals("taken")) {
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
    class LoginWebServiceTask extends AsyncTask<String, Void, String> {
        private final String SERVICE = "_login.php";
        private ProgressDialog pDialog;
        private String mUsername;

        public LoginWebServiceTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Logging in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

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

            Log.d("doInBackground", "Finished. reponse = " + response);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }

            if (result.equals("success")) {

                //succesful login
                Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_LONG)
                        .show();

                //set active user
                ACTIVE_USER = mUsername;

                // automatically search for events near current location when logging in
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                GetPrefsWebServiceTask task = new GetPrefsWebServiceTask();
                task.execute(DB_URL, settings.getString(GET_USERNAME, ""));

            } else if (result.equals("fail")) {
                //unsuccessful login
                Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_LONG)
                        .show();
                SharedPreferences sharedSettings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedSettings.edit();
                //clear any incorrect stuff
                editor.remove(GET_USERNAME);
                editor.remove(GET_PASSWORD);
            }
        }
    }

    /**
     * Asynctask that fetches the active user's prefs,
     * and opens a home fragment with a corresponding search
     *
     * @author Evan Pernu
     */
    class GetPrefsWebServiceTask extends AsyncTask<String, Void, String> {
        private final String SERVICE = "getPrefs.php";
        private ProgressDialog pDialog;
        private String mUsername;

        public GetPrefsWebServiceTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Getting your preferences...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length != 2) {
                throw new IllegalArgumentException("Two String arguments required.");
            }

            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];

            //set the active username it knows who to log in later
            mUsername = strings[1];

            try {
                URL urlObject = new URL(url + SERVICE + "?my_u="+mUsername);

                urlConnection = (HttpURLConnection) urlObject.openConnection();

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

            //Log.d("getPrefs", "Finished. reponse = " + response);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }else{

                PrefList mEvents = new PrefList();
                //make an object of type PrefList with the prefs data
                try {
                    mEvents = PrefList.JSONFactory(new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //search for events that positively match the user's prefs
                new EventSearchService(context, "Finding events...").execute(EVENTBRITE_URL + "?location.latitude="
                        + String.valueOf(mCurrentLocation.getLatitude()) + "&location.longitude="
                        + String.valueOf(mCurrentLocation.getLongitude()) + "&location.within=" + DEFUALT_SEARCH_RADIUS + "&token="
                        + EVENTBRITE_KEY + "&expand=venue"+"&categories="+mEvents.getPreferredString());
            }
        }
    }

    /**
     * Builds a map of every category as well as its EventBrite ID.
     * These values are hard coded to eliminate the need for another api call.
     *
     * @return a map of every category as well as its EventBrite ID
     */
    public static Map<String, Integer> initCategories() {
        Map<String, Integer> mCategories = new HashMap<String, Integer>();

        mCategories = new HashMap<String, Integer>();
        mCategories.put("Music", 103);
        mCategories.put("Business & Professional", 101);
        mCategories.put("Food & Drink", 110);
        mCategories.put("Community & Culture", 113);
        mCategories.put("Performing & Visual Arts", 105);
        mCategories.put("Film, Media & Entertainment", 104);
        mCategories.put("Sports & Fitness", 108);
        mCategories.put("Health & Wellness", 107);
        mCategories.put("Science & Technology", 102);
        mCategories.put("Travel & Outdoor", 109);
        mCategories.put("Charity & Causes", 111);
        mCategories.put("Religion & Spirituality", 114);
        mCategories.put("Family & Education", 115);
        mCategories.put("Seasonal & Holiday", 116);
        mCategories.put("Government & Politics", 112);
        mCategories.put("Fashion & Beauty", 106);
        mCategories.put("Home & Lifestyle", 117);
        mCategories.put("Auto, Boat & Air", 118);
        mCategories.put("Hobbies & Special Interest", 119);
        mCategories.put("Other", 199);

        return mCategories;
    }

    /**
     * helper method that converts EventBrite category names to their corresponding ID numbers.
     *
     * @param categoryList a List of category names
     * @return a list of corresponding ID numbers
     */
    public static ArrayList<Integer> convertCategories(ArrayList<String> categoryList){
        ArrayList<Integer> res = new ArrayList<Integer>();

        for(String s : initCategories().keySet()){
            res.add(initCategories().get(s));
        }
        return res;
    }

    /**
     * private helper that fetches current location in EventBrite URL form
     * @return current location in EventBrite URL form
     */
    private String getCurrentLocation(){
        return "&location.latitude=" + String.valueOf(mCurrentLocation.getLatitude()) +
                "&location.longitude=" + String.valueOf(mCurrentLocation.getLongitude());
    }
}
