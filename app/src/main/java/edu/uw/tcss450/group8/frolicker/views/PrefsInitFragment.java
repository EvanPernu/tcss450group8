package edu.uw.tcss450.group8.frolicker.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.PrefsInitAdapter;
import edu.uw.tcss450.group8.frolicker.model.PrefList;

/**
 * This Fragment displays a list of event categories and prompts the user to choose
 * whether they like, dislike, or are impartial to each one. It then turns this information into a JSONObject
 * with a format compliant to PrefList.java and sends it to our database. This fragment is ment to be called
 * once upon a user's initial registration.
 *
 * @author Evan Pernu
 * @version 5/2/2017
 */
public class PrefsInitFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private PrefsInitAdapter mPrefsInitAdapter;
    private RecyclerView mPrefsInitRecyclerView;

    /** The list of event categories the user will be asked about*/
    private final String[] DEFAULT_PREFS = {"Comedy", "Sports", "Music", "Fair",
            "Culture", "Charity", "Historical", "Theatre", "Dance", "Outdoors"};

    /**Default constructor*/
    public PrefsInitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prefs_init, container, false);

        //Set up the RecyclerView
        mPrefsInitRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mPrefsInitAdapter = new PrefsInitAdapter(getDefaultOptionsMap(), this);
        mPrefsInitRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPrefsInitRecyclerView.setAdapter(mPrefsInitAdapter);
        mPrefsInitRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPrefsInitAdapter.notifyDataSetChanged();

        //mListener.onPrefsInitFragmentInteraction("upload", null);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
}

    /**
     * This method is called when the user has provided their opinion on every category.
     * The MainActivity will be prompted to upload the user's preferences to the database.
     * @param theMap
     */
    public void notifyDone(Map<String, Integer> theMap) throws JsonProcessingException {
        PrefList thePrefs = new PrefList(theMap);
        ObjectMapper mapper = new ObjectMapper();

        //JSONObject theJSO = new JSONObject();
        mListener.onPrefsInitFragmentInteraction("upload", mapper.writeValueAsString(theMap));
        Log.d("notifyDone", "Notified Main to upload the following String to database: \n"+mapper.writeValueAsString(theMap));
    }

     /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onPrefsInitFragmentInteraction(String s, String theJSString);
    }

    /**
     * returns the  default list of pref init options in map form
     * @return default list of pref init options
     */
    private Map<String, Integer> getDefaultOptionsMap(){
        Map<String, Integer> result = new HashMap<String, Integer>();

        for(int i = 0; i < DEFAULT_PREFS.length; i++) {
            result.put(DEFAULT_PREFS[i], 0);
        }
        return result;
    }
}
