package edu.uw.tcss450.group8.frolicker.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView mActiveUser;
    private OnFragmentInteractionListener mListener;
    private String mUsername;

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
        setHasOptionsMenu(true);
        Bundle args = this.getArguments();
        if (args != null) {
            mUsername = args.getString("name");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //set onclick listeners
//        Button b = (Button) rootView.findViewById(R.id.btnSearchAnyEvents);
//        b.setOnClickListener(this);

        //create a reference to user display
        mActiveUser = (TextView) rootView.findViewById(R.id.activeUser);
        mActiveUser.setText("Welcome, "+mUsername+"!");

        //set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.event_card_recycler_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        EventAdapter eventAdapter = new EventAdapter(getContext(), eventCardList, recyclerView);
        eventAdapter.setHasStableIds(true);
        recyclerView.setAdapter(eventAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        MainActivity mainActivity;
        switch (item.getItemId()) {
            case R.id.view_map:
                loadMap();
                break;

            case R.id.new_search:
                mainActivity = (MainActivity)getContext();
                mainActivity.loadNextEventFragment(eventCardList,0);
                break;

            case R.id.logout:
                mainActivity = (MainActivity)getContext();
                mainActivity.onFragmentInteraction(6);
            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMap() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Map...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                MainActivity mainActivity = (MainActivity)getContext();
                mainActivity.loadNextEventFragment(eventCardList,2);
            }
        }, 1000);
    }

    @Override
    public void onClick(final View view)
    {
//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        //progressDialog.setMessage("Loading...");
//
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                progressDialog.dismiss();
//                if(mListener != null) {
//                    switch (view.getId()) {
//                        case R.id.btnSearchAnyEvents:
//                            MainActivity mainActivity = (MainActivity)getContext();
//                            mainActivity.loadNextEventFragment(eventCardList,3);
//                            //mainActivity.loadHomeFragment(eventCardList);
//                            //mListener.onHomeFragmentInteraction(1);
//                            break;
//                    }
//                }
//
//
//            }
//        }, 1000);

//        if (mListener != null) {
//            switch (view.getId()) {
//                case R.id.btnSearchAnyEvents:
//                    MainActivity mainActivity = (MainActivity)getContext();
//                    //mainActivity.loadEventMapFragment(eventCardList);
//                    mainActivity.loadHomeFragment(eventCardList);
//                    //mListener.onHomeFragmentInteraction(1);
//                    break;
//            }
        }
 //   }


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