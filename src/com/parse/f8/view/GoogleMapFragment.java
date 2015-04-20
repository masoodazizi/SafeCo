package com.parse.f8.view;

import java.io.IOException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.parse.f8.AddressConverter;
import com.parse.f8.R;
import com.parse.f8.R.layout;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class GoogleMapFragment extends Fragment {

	public static final String STATUS_UPDATE_PREFS = "statusUpdatePrefs";
	static final LatLng INFORMATIC_LOC = new LatLng(50.778396, 6.060989);
	private GoogleMap map;
	private Marker currentLocMarker = null;
	private ImageView checkButton;
	private TextView textMapAddress;
	private String latitudeSelected = "null";
	private String longitudeSelected = "null";
	private static View googleMapView;
	
	public GoogleMapFragment() {
		// Required empty public constructor
	}


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
	    if (googleMapView != null) {
	        ViewGroup parent = (ViewGroup) googleMapView.getParent();
	        if (parent != null)
	            parent.removeView(googleMapView);
	    }
	    try {
	    	googleMapView = inflater.inflate(R.layout.fragment_googlemap, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
//		googleMapView = inflater.inflate(R.layout.fragment_googlemap, container, false);
	    
		textMapAddress = (TextView) googleMapView.findViewById(R.id.text_map_address);
		
		setUpMap();
		saveStatusUpdatePref(latitudeSelected, longitudeSelected);
		
		map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				
				latitudeSelected = Double.toString(latLng.latitude);
				longitudeSelected = Double.toString(latLng.longitude);
				saveStatusUpdatePref(latitudeSelected, longitudeSelected);
				showMarkerOnMap(latLng, "Your Selected Location");
				printSelectedAddress(latLng);

			}
		});
		
		checkButton = (ImageView) googleMapView.findViewById(R.id.image_check);
		checkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (currentLocMarker != null) {
					currentLocMarker.remove();
				}
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		
		return googleMapView;
	}
	
//	private void setUpMapIfNeeded() {
//		
//		 // Do a null check to confirm that we have not already instantiated the map.
//	    if (map == null) {
//	        // Try to obtain the map from the SupportMapFragment.
//	        map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.googlemap))
//	                .getMap();
//	        map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.googlemap)).getMap();
//	        // Check if we were successful in obtaining the map.
//	        if (map != null) {
//	            setUpMap();
//	        }
//	    }
//	}
	
	private void setUpMap() {
		
		LatLng latLng = INFORMATIC_LOC;
		String locTitle = "Default Location";
//		map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.googlemap)).getMap();
        map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.googlemap))
        .getMap();
		map.setMyLocationEnabled(true);
		map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location currentLoc = locationManager.getLastKnownLocation(provider);
		if (currentLoc != null) {
			// FIXME currentLoc returns NULL!!!
			double latitude = currentLoc.getLatitude();
			double longitude = currentLoc.getLongitude();
			latLng = new LatLng(latitude, longitude);
			locTitle = "Your Current Location";
		} 
		showMarkerOnMap(latLng, locTitle);
	}

	private void showMarkerOnMap(LatLng latLng, String locTitle) {
		
		// TASK load program icon but in smaller size. Normal size is pretty large.
		// add => .icon(BitmapDescriptorFactory.fromResource(R.drawable.appicon))
		removeMapItems();
		
		currentLocMarker = map.addMarker(new MarkerOptions().position(latLng)
							.title(locTitle).icon(BitmapDescriptorFactory.fromResource(R.drawable.appicon_marker)));
	    // Move the camera instantly to hamburg with a zoom of 15.
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
	
	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	}
	
	private void saveStatusUpdatePref(String latitude, String longitude) {
		
		SharedPreferences statusUpdatePref = this.getActivity().getSharedPreferences(STATUS_UPDATE_PREFS, 0);
	    SharedPreferences.Editor editor = statusUpdatePref.edit();
	    editor.putString("latitude" , latitude);
	    editor.putString("longitude" , longitude);
		editor.commit();
		
	}
	
	private void printSelectedAddress(LatLng latlng) {
		
		AddressConverter addressConverter = new AddressConverter
				(getActivity().getApplicationContext(), latlng.latitude, latlng.longitude);
		String address = "Address not fetchted!";
		address = addressConverter.getAddress();

		textMapAddress.setText(address);
	}
	
	@Override
	public void onResume() {
        super.onResume();
        setUpMap();
    }
	
	private void removeMapItems() {
		
		if (currentLocMarker != null) {
			currentLocMarker.remove();
		}
		textMapAddress.setText("No address selected...");
	}
	
	@Override
	public void onDestroyView() {
		
		removeMapItems();
		super.onDestroyView();
	}

}
