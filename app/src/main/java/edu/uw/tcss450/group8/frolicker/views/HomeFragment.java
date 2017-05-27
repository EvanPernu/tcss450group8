package edu.uw.tcss450.group8.frolicker.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import edu.uw.tcss450.group8.frolicker.model.EventAdapter;
import edu.uw.tcss450.group8.frolicker.model.EventCard;

/**
 * This fragment is displayed once the user logs in.
 * It displays a list of reccomended nearby events as well as
 * buttons to navigate to every other fragment.
 *
 * @author Evan Pernu
 */
public class HomeFragment extends Fragment  implements View.OnClickListener {

    private List<EventCard> eventCardList;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    /**
     *
     * @param name the active user's username
     * @return
     */
    public static HomeFragment newInstance(String name) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Button b = (Button) rootView.findViewById(R.id.btnSearchAnyEvents);
        b.setOnClickListener(this);

        //set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.event_card_recycler_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        EventAdapter eventAdapter = new EventAdapter(getContext(), eventCardList, recyclerView);
        eventAdapter.setHasStableIds(true);
        recyclerView.setAdapter(eventAdapter);

        return rootView;
    }

    public void setEventCardList(List<EventCard> eventCardList) {
        this.eventCardList = eventCardList;
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

    @Override
    public void onClick(View view)
    {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.btnSearchAnyEvents:
                    mListener.onHomeFragmentInteraction(1);
                    break;
            }
        }
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
        // TODO: Update argument type and name
        void onHomeFragmentInteraction(int n);
    }

}

//<android.support.v7.widget.RecyclerView
//        android:id="@+id/event_card_recycler_home"
//        android:layout_width="108dp"
//        android:layout_height="wrap_content"
//        android:layout_marginBottom="8dp"
//        android:layout_marginLeft="8dp"
//        android:layout_marginRight="8dp"
//        android:layout_marginTop="8dp"
//        app:layout_constraintBottom_toTopOf="@+id/tableLayout"
//        app:layout_constraintLeft_toLeftOf="parent"
//        app:layout_constraintRight_toRightOf="parent"
//        app:layout_constraintTop_toBottomOf="@+id/textView" />