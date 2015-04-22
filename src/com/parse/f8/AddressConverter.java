package com.parse.f8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class AddressConverter {
	
    double latitude;
    double longitude;
    Context context;
    Geocoder geocoder;
    List<Address> addresses;
    Address firstAddress;
    int maxIndex;

    public AddressConverter(Context appContext, double lat, double lng) {
		
    	context = appContext;
    	latitude = lat;
    	longitude = lng;
        geocoder = new Geocoder(context);
        if (latitude != 0 || longitude != 0) {
        	
            try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("AddrCnvr", "The address is Null");
				firstAddress = null;
				return;
			}
            firstAddress = addresses.get(0);
            maxIndex = firstAddress.getMaxAddressLineIndex();
            
        } else {
            firstAddress = null;
        }
	}
    
    public String getAddress() {
    	
    	if (firstAddress != null) {
    		
	        String addr = "";
	        for (int i=0;i<=maxIndex;i++) {
		       	 if (i==0) {
		       		 addr = firstAddress.getAddressLine(i);
		       	 }
		       	 else {
		           	 if (firstAddress.getAddressLine(i) != null) {
		           		 addr =addr + ", " + firstAddress.getAddressLine(i);
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
    
    public String generalizeFirstLevel() {
    	
    	String addr = "";
    	if (firstAddress != null) {
    		
	        ArrayList<String> addrArray = new ArrayList<>();
	        int addrIndex = maxIndex;
	        for (int i=0 ; i<3 ; i++) {
	        	addrArray.add(firstAddress.getAddressLine(addrIndex));
	        	addrIndex--;
	        }
	        int lastIndex = addrArray.size()-1;
	        String filteredAddr = removeStreetNum(addrArray.get(lastIndex));
	        addrArray.set(lastIndex, filteredAddr);
	        for (int i=lastIndex ; i>=0 ; i--) {
				if (i==lastIndex) {
					addr = addrArray.get(i);
				}
				else {
			   		addr =addr + ", " + addrArray.get(i);
				   	}
				}
	        return addr;
	        }
    	 else {
    		Log.e("Error", "latitude and longitude are null");
    		return null;
    	}
    }
    
    public String generalizeSecondLevel() {
    	
    	String addr = "";
    	if (firstAddress != null) {
    		
	        ArrayList<String> addrArray = new ArrayList<>();
	        int addrIndex = maxIndex;
	        for (int i=0 ; i<2 ; i++) {
	        	addrArray.add(firstAddress.getAddressLine(addrIndex));
	        	addrIndex--;
	        }
	        int lastIndex = addrArray.size()-1;
	        String filteredAddr = removePostalCode(addrArray.get(lastIndex));
	        addrArray.set(lastIndex, filteredAddr);
	        for (int i=lastIndex ; i>=0 ; i--) {
				if (i==lastIndex) {
					addr = addrArray.get(i);
				}
				else {
			   		addr =addr + ", " + addrArray.get(i);
				   	}
				}
	        return addr;
	        }
    	 else {
    		Log.e("Error", "latitude and longitude are null");
    		return null;
    	}
    }
    
    private String removeStreetNum(String street) {
    	
    	String filteredAddr = "No Filtered Address";
    	List<String> addrList = Arrays.asList(street.split(" "));
    	for (int i =0 ; i<addrList.size()-1 ; i++) {
    		if (i==0) {
    			filteredAddr = addrList.get(i);
			}
			else {
				filteredAddr =filteredAddr + " " + addrList.get(i);
			   	}
    	}
    	return filteredAddr;
    		
    }
    
    private String removePostalCode(String citycode) {
    	
    	String filteredAddr = "No Filtered Address";
    	List<String> addrList = Arrays.asList(citycode.split(" "));
    	for (int i =1 ; i<addrList.size() ; i++) {
    		if (i==1) {
    			filteredAddr = addrList.get(i);
			}
			else {
				filteredAddr =filteredAddr + " " + addrList.get(i);
			   	}
    	}
    	return filteredAddr;
    		
    }
}
