package edu.uw.tcss450.group8.frolicker.views;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.EventCard;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    List<EventCard> eventCardList;

    public EventMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_event_map, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if(mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        for(int i=0; i<eventCardList.size(); i++) {
            double lat = Double.parseDouble(eventCardList.get(i).getEventLatitude());
            double lng = Double.parseDouble(eventCardList.get(i).getEventLongitude());
            String name = eventCardList.get(i).getEventName();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                    .snippet(eventCardList.get(i).getEventVenue()));
        }

        // using the first event in the list for camera positioning
        double lat1 = Double.parseDouble(eventCardList.get(0).getEventLatitude());
        double lng1 = Double.parseDouble(eventCardList.get(0).getEventLongitude());

        CameraPosition cam = CameraPosition.builder().target(new LatLng(lat1,lng1)).zoom(10)
                .bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam));

    }

    public void setEventCardList(List<EventCard> eventCardList) {
        this.eventCardList = eventCardList;
    }
}
