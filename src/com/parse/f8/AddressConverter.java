package com.parse.f8;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class AddressConverter {
	
    double latitude;
    double longitude;
    Context context;

    public AddressConverter(Context appContext, double lat, double lng) {
		
    	context = appContext;
    	latitude = lat;
    	longitude = lng;
	}
    
    public String getAddress() throws IOException {
    	
    	Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context);
        if (latitude != 0 || longitude != 0) {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String addr = "";
            Address firstAddress = addresses.get(0);
            for (int i=0;i<5;i++) {
           	 if (i==0) {
           		 addr = firstAddress.getAddressLine(i);
           	 }
           	 else {
               	 if (firstAddress.getAddressLine(i) != null) {
               		 addr =addr + " , " + firstAddress.getAddressLine(i);
               	 }
               	 else {
               		 break;
               	 }
           	 }
            }
            return addr;
        } else {
            Log.e("Error", "latitude and longitude are null");
            return null;
        }
    }

}
