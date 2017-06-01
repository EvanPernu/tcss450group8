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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import edu.uw.tcss450.group8.frolicker.R;
import edu.uw.tcss450.group8.frolicker.model.EventCard;


/**
 * The type Event map fragment.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private List<EventCard> eventCardList;
    private double lat;
    private double lng;
    private String name;

    /**
     * Instantiates a new Event map fragment.
     */
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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        for(int i=0; i<eventCardList.size(); i++) {
            lat = Double.parseDouble(eventCardList.get(i).getEventLatitude());
            lng = Double.parseDouble(eventCardList.get(i).getEventLongitude());
            name = eventCardList.get(i).getEventName();
            addMapIcon(eventCardList.get(i).getEventCategoryId(), i);
        }

        // using the first event in the list for camera positioning
        double lat1 = Double.parseDouble(eventCardList.get(0).getEventLatitude());
        double lng1 = Double.parseDouble(eventCardList.get(0).getEventLongitude());

        CameraPosition cam = CameraPosition.builder().target(new LatLng(lat1,lng1)).zoom(10)
                .bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam));

    }

    /**
     * Sets event card list.
     *
     * @param eventCardList the event card list
     */
    public void setEventCardList(List<EventCard> eventCardList) {
        this.eventCardList = eventCardList;

    }

    /**
     * Add map icon.
     *
     * @param id    the id
     * @param index the index
     */
    private void addMapIcon(String id, int index) {

        switch (id) {

            case "103":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.music)));
                break;

            case "110":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));
                break;

            case "102":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.tech)));
                break;

            case "111":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.charity)));
                break;

            case "106":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.fashion)));
                break;

            case "108":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sports)));
                break;

            case "104":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.game)));
                break;

            case "105":
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.artss)));
                break;

            default:
                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(name)
                        .snippet(eventCardList.get(index).getEventVenue()));
                break;
        }
    }
}
