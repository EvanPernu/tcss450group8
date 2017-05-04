package edu.uw.tcss450.group8.frolicker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import groupn.tcss450.uw.edu.frolicker2.Model.Event;



/**
 * A simple {@link Fragment} subclass.
 */
public class EventSearchResultFragment extends Fragment {

    private List<Event> eventList;
    private TextView textView;
    private ListView listView;

    public EventSearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        showResults();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_search_result, container, false);

        listView = (ListView) view.findViewById(R.id.eventMenu);

        return view;
    }

    public void showResults() {
        ArrayList<String> names = new ArrayList<>();
        for(int i=0; i<eventList.size(); i++) {
            //Log.d(TAG, "show: " + eventList.get(i).getEventName());
            names.add(eventList.get(i).getEventName());
        }

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                names
        );

        listView.setAdapter(listViewAdapter);
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

}
