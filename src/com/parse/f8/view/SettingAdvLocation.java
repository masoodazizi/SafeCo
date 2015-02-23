package com.parse.f8.view;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.f8.AddressConverter;
import com.parse.f8.R;
/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class SettingAdvLocation extends Fragment {
	
	public static final String ADV_SETTING_PREFS = "AdvSettingPrefs";
	static final LatLng INFORMATIC_LOC = new LatLng(50.778396, 6.060989);
	private GoogleMap map;
	private TextView textAdvLocAddr;
	private Marker currentLocMarker = null;
	private Boolean enableKey = true;

	public SettingAdvLocation() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View advLocView = inflater.inflate(R.layout.setting_advlocation, container, false);
		
		setUpMap();
		textAdvLocAddr = (TextView) advLocView.findViewById(R.id.txt_advLocAddr);
		onSwitchClicked(advLocView);
		
		map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				
				if (enableKey) {
					//textAdvLocAddr.setText(latLong.toString());
					String address = "Address not fetched!";
					AddressConverter addressConverter = new AddressConverter
							(getActivity().getApplicationContext(), latLng.latitude, latLng.longitude);
					try {
						address = addressConverter.getAddress();
					} catch (IOException e) {
						e.printStackTrace();
					}
//					String address = getAddress(latLng.latitude, latLng.longitude);
					textAdvLocAddr.setText(address);
					saveAdvSettingPref("locationAddr", address);
					saveAdvSettingPref("locationGeo", latLng.toString());
					showMarkerOnMap(latLng, "Your Selected Location");
				}
			}
		});
		
		return advLocView;
	}

	private void setUpMap() {
		
		LatLng latLng = INFORMATIC_LOC;
		String locTitle = "Default Location";
		map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
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
		if (currentLocMarker != null) {
			currentLocMarker.remove();
		}
		
		currentLocMarker = map.addMarker(new MarkerOptions().position(latLng)
							.title(locTitle));
	    // Move the camera instantly to hamburg with a zoom of 15.
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	}
	

	
	private void saveAdvSettingPref(String type, String value) {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    editor.putString(type , value);
		editor.commit();
		
	}
	
	public void onSwitchClicked(final View v) {
		
		Switch disableSwitch = (Switch) v.findViewById(R.id.switch_location);
		disableSwitch.setChecked(true);
		disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					enableKey = true;		
				}
				else {
					
					textAdvLocAddr.setText("");
					enableKey = false;
					removePrefsKeys();
				}
			}
		});

	}
	
	private void removePrefsKeys() {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    editor.remove("locationAddr");
	    editor.remove("locationGeo");
		editor.commit();
	}
	
}

///////////////////////////////////////////////////////////////////////////////




//private String getAddress(double latitude, double longitude) {
//try {
//  Geocoder geocoder;
//  List<Address> addresses;
//  geocoder = new Geocoder(getActivity().getApplicationContext());
//  if (latitude != 0 || longitude != 0) {
//      addresses = geocoder.getFromLocation(latitude, longitude, 1);
////             String address = addresses.get(0).getAddressLine(0);
////             String city = addresses.get(0).getAddressLine(1);
////             String country = addresses.get(0).getAddressLine(2);
//      String addr = "";
//      Address firstAddress = addresses.get(0);
//      for (int i=0;i<5;i++) {
//     	 if (i==0) {
//     		 addr = firstAddress.getAddressLine(i);
//     	 }
//     	 else {
//         	 if (firstAddress.getAddressLine(i) != null) {
//         		 addr =addr + " , " + firstAddress.getAddressLine(i);
//         	 }
//         	 else {
//         		 break;
//         	 }
//     	 }
//      }
////             String fullAddress = address + " , " + city + ", country = "+country;
////                         Log.d("MyDebug", fullAddress);
//      return addr;
//  } else {
//      Toast.makeText(getActivity().getApplicationContext(), "latitude and longitude are null",
//              Toast.LENGTH_LONG).show();
//      return null;
//  }
//} catch (Exception e) {
//  e.printStackTrace();
//  return null;
//}
//}


//class MapOverlay extends com.google.android.maps.Overlay
//{
////    @Override
////    public boolean draw(Canvas canvas, MapView mapView, 
////    boolean shadow, long when) 
////    {
////       //...
////    }
//
////    @Override
//    public boolean onTouchEvent(MotionEvent event, MapView mapView) 
//    {   
//        //---when user lifts his finger---
//        if (event.getAction() == 1) {                
//            GeoPoint p = mapView.getProjection().fromPixels(
//                (int) event.getX(),
//                (int) event.getY());
//                Log.d("MyDebug", p.getLatitudeE6() / 1E6 + "," + p.getLongitudeE6() /1E6);
//        }                            
//        return false;
//    }        
//}


/////////////////////////////////  Extra Useless Codes   //////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////



//private void getCurrentLocation () {
//	
//	mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//            mGoogleApiClient);
//    if (mLastLocation != null) {
//        mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//        mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//    }
//}
//
//protected synchronized void buildGoogleApiClient() {
//    mGoogleApiClient = new GoogleApiClient.Builder(this)
//        .addConnectionCallbacks(this)
//        .addOnConnectionFailedListener(this)
//        .addApi(LocationServices.API)
//        .build();
//}

//class GetCurrentLocation implements ConnectionCallbacks, OnConnectionFailedListener {
//
//	@Override
//	public void onConnectionFailed(ConnectionResult arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onConnected(Bundle arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onDisconnected() {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	
//}