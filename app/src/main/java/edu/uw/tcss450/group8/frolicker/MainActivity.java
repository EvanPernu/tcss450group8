package edu.uw.tcss450.group8.frolicker;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity
        implements
        LoginOrRegisterFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        PrefsInitFragment.OnFragmentInteractionListener{

    //The URL of the database
    private final String DB_URL = "http://cssgate.insttech.washington.edu/~_450agrp8/";

    //Tracks the username of the active user
    private String ACTIVE_USER = "Evan"; //change later

    //The URL of login
    private static final String PARTIAL_LOGIN_URL = "http://cssgate.insttech.washington.edu/"
            + "~_450agrp8/feedback";

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

                // Commit the transaction
                secondTransaction.commit();
                break;
            case 3:
                RegisterFragment registerFragment = new RegisterFragment();

                FragmentTransaction thirdTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, registerFragment)
                        .addToBackStack(null);

                // Commit the transaction
                thirdTransaction.commit();
                break;
            case 4:
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
     */
    private class RegisterWebServiceTask extends AsyncTask<String, Void, String> {
        private final String SERVICE = "_register.php";

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }

            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];

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

            //            GetSetlistFragment getSetlistFragment = new GetSetlistFragment();
//            FragmentTransaction getSetlistTransaction = getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragmentContainer, getSetlistFragment)
//                    .addToBackStack(null);
//
//            // Commit the transaction
//            getSetlistTransaction.commit();
            //wherever we wanna go
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

        @Override
        protected String doInBackground(String... strings) {

            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }

            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];

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

            Log.d("onPostExecute", "Making next fragment");

            if(result.equals("success")){
                //successful login

                FragmentTransaction loginTransaction = getSupportFragmentManager().beginTransaction()
                   .replace(R.id.fragmentContainer, new EventSearchFragment())
                   .addToBackStack(null);

           // Commit the transaction
            loginTransaction.commit();

            }else if(result.equals("fail")){
                //unsuccessful login

                //TODO raise toast

            }
        }
    }
}
