package edu.uw.tcss450.group8.frolicker.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.EventAdapter;
import edu.uw.tcss450.group8.frolicker.model.EventCard;


public class EventCardRecycler extends Fragment {

    private List<EventCard> eventCardList;

    public EventCardRecycler() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recycler_event_card, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.event_card_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        EventAdapter eventAdapter = new EventAdapter(getContext(), eventCardList, recyclerView);
        eventAdapter.setHasStableIds(true);
        recyclerView.setAdapter(eventAdapter);
        return rootView;
    }



    public void setEventCardList(List<EventCard> eventCardList) {
        this.eventCardList = eventCardList;
    }



}