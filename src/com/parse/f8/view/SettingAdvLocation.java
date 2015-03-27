package com.parse.f8.view;

import java.io.IOException;
import java.sql.Savepoint;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
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
	private static View advLocView;

	public SettingAdvLocation() {
		// Required empty public constructor
	}
	// TASK Add radiobutton to have an option to show first level generalization
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if (advLocView != null) {
			ViewGroup parent = (ViewGroup) advLocView.getParent();
			if (parent != null) {
				parent.removeView(advLocView);
			}
		}
		try {
			advLocView = inflater.inflate(R.layout.setting_advlocation, container, false);
		} catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
				
		textAdvLocAddr = (TextView) advLocView.findViewById(R.id.txt_advLocAddr);
		
		setUpMap();
		onSwitchClicked(advLocView);
		onOKClicked(advLocView);
		onHelpClicked(advLocView);
		
		map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				
				if (enableKey) {
					//textAdvLocAddr.setText(latLong.toString());
					String address = "Address not fetched!";
					AddressConverter addressConverter = new AddressConverter
							(getActivity().getApplicationContext(), latLng.latitude, latLng.longitude);
					address = addressConverter.getAddress();
					String address1 = addressConverter.generalizeFirstLevel();
					Log.d("MXAddrGen", address1);
					String address2 = addressConverter.generalizeSecondLevel();
					Log.d("MXAddrGen", address2);
//					String address = getAddress(latLng.latitude, latLng.longitude);
					textAdvLocAddr.setText(address);
					saveAdvSettingPref("locationAddr", address);
					saveAdvSettingPref("locationGeo", latLng.toString());
					saveAdvSettingPref("latitude", Double.toString(latLng.latitude));
					saveAdvSettingPref("longitude", Double.toString(latLng.longitude));
					showMarkerOnMap(latLng, "Your Selected Location");
				}
			}
		});
		
		return advLocView;
	}

	private void setUpMap() {
		
		LatLng latLng = INFORMATIC_LOC;
		String locTitle = "Default Location";
//		map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
		map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
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
		// Check if location flag is true, load values from shared preferences
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
		if (advSettingPref.getBoolean("locationFlag", false)) {
			
			textAdvLocAddr.setText(advSettingPref.getString("locationAddr", "null"));
			double latPrefs = Double.parseDouble(advSettingPref.getString("latitude", "0"));
			double lngPrefs = Double.parseDouble(advSettingPref.getString("longitude", "0"));
			latLng = new LatLng(latPrefs, lngPrefs);
			locTitle = "Your saved location";
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
	    editor.putBoolean("locationFlag", true);
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
	
	private void onOKClicked(View v) {
		
		ImageView imageOK = (ImageView) v.findViewById(R.id.image_loc_OK);
		imageOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
	}
	
	private void onHelpClicked(View v) {
		
		ImageView imageHelp = (ImageView) v.findViewById(R.id.image_loc_help);
		imageHelp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.setTitle("Help");
				alertDialog.setMessage(getResources().getString(R.string.help_location));
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				            dialog.dismiss();
				        }
				    });
				alertDialog.show();
			}
		});
	}
	
	private void removePrefsKeys() {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    editor.remove("locationAddr");
	    editor.remove("locationGeo");
	    editor.remove("latitude");
	    editor.remove("longitude");
	    editor.putBoolean("locationFlag", false);
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