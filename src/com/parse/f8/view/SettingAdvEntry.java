package com.parse.f8.view;

import java.util.Arrays;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.f8.R;
import com.parse.f8.R.layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
// FIXMED widgets from background fragment work on the foreground active fragment!
// FIXME Add help dialog to all advanced pages
// FIXME Add OK button to all items
// FIXME Add EDIT entry activity and necessary modification to existing items
// TASK load data in each item from prefs if it's already set

public class SettingAdvEntry extends Fragment {

	public static final String ADV_SETTING_PREFS = "AdvSettingPrefs";
	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	public static final String PARSE_ADV_PRIVACY_CLASS = "RestrictedList";
	String[] advEntryListItems;
	Button buttonAddEntry;
	private String userId;
	private String userName;
	
	
	public SettingAdvEntry() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View advEntryView = inflater.inflate(R.layout.setting_adventry, container, false);
		userId = fetchUserInfo("fbId");
		initialiazeFlags();
		
		advEntryListItems = getResources().getStringArray(R.array.adventry_list_items);
		ListView advEntryListView = (ListView) advEntryView.findViewById(R.id.listAdvEntry);
		ListAdapter advEntryAdapter = new ArrayAdapter<String>
					(getActivity(), R.layout.entry_item, R.id.lbl_entryItemTitle ,advEntryListItems);
		advEntryListView.setAdapter(advEntryAdapter);
		// TASK create a manual adapter to set the item info on the list (e.g: "Not Set")
		
		advEntryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Fragment newFragment = null;
				Bundle bundle = new Bundle();
				Log.d("myDebug", "Selected Item:" + Integer.toString(position) );
				
				switch (position) {
				case 0:
					newFragment = new SettingAdvTime();
					break;
				case 1:
					newFragment = new SettingAdvLocation();
					break;
				case 2:
					newFragment = new SettingAdvCoUser();
					bundle.putString("key", "coUser");
					newFragment.setArguments(bundle); 
					break;
				case 3:
					newFragment = new SettingAdvCoUser();
					bundle.putString("key", "viUser");
					newFragment.setArguments(bundle); 
					break;
				case 4:
					newFragment = new SettingFragment();
					bundle.putString("key", "add");
					newFragment.setArguments(bundle); 
					break;
				}
				
				Log.i("BUNDLE", bundle.toString());
			    FragmentTransaction transaction = getFragmentManager().beginTransaction();
			    transaction.replace(R.id.fragment_adventry, newFragment);
			    transaction.addToBackStack(null);
			    transaction.setTransition(4099);	
			    transaction.commit(); 
			}
			
			
		});
		
		buttonAddEntry = (Button) advEntryView.findViewById(R.id.btn_addEntry);
		buttonAddEntry.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				saveAdvPrefsInfoToParse();
			}
		});
		
		
		return advEntryView;
	}
	
	private void saveAdvPrefsInfoToParse() {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
		
//		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_ADV_PRIVACY_CLASS);
//		query.whereEqualTo("userId", userId);
//		query.findInBackground(new FindCallback<ParseObject>() {
//			
//			@Override
//			public void done(List<ParseObject> userObj, ParseException e) {
//				
//		        if (e == null) {
//		        	
//		        	if (userObj == null) {
//		        		Log.d("ParseQueryError", "There is no user object with user ID " + userId + 
//		        				"is defined in <" + PARSE_ADV_PRIVACY_CLASS + "> Parse Class");
//		        	}
//		        	else if (checkFalseFlags()) {
//		        		Toast.makeText(getActivity().getApplicationContext(), "Error: No data is selected!", 
//								Toast.LENGTH_SHORT).show();
//		        	}
//		        	else {
//		        		// Put PRIVACY PREFs data on Parse Object
//		        		ParseObject user = userObj.get(0);
		ParseObject advParseObj = new ParseObject(PARSE_ADV_PRIVACY_CLASS);
		userName = fetchUserInfo("firstName");
		
		advParseObj.put("userId", userId);
		advParseObj.put("user", userName);
		
		advParseObj.put("identityLvl", advSettingPref.getInt("identityLvl", 0));
		advParseObj.put("timeLvl", advSettingPref.getInt("timeLvl", 0));
		advParseObj.put("locationLvl", advSettingPref.getInt("locationLvl", 0));
		
		// Put TIME data on Parse Object
		advParseObj.put("dayOfWeek", advSettingPref.getInt("dayOfWeek", 0));
		advParseObj.put("exactDate", advSettingPref.getString("exactDate", "null"));
		advParseObj.put("timePeriod", advSettingPref.getString("timePeriod", "null"));
		advParseObj.put("timeStart2", advSettingPref.getString("timeStart", "null"));
		advParseObj.put("timeEnd2", advSettingPref.getString("timeEnd", "null"));
		advParseObj.put("timeDayPart", advSettingPref.getString("timeDayPart", "null"));
		
		// Put LOCATION data on Parse Object
		advParseObj.put("locationAddr", advSettingPref.getString("locationAddr", "null"));
		double latitude = Double.parseDouble(advSettingPref.getString("latitude", "0"));
		double longitude = Double.parseDouble(advSettingPref.getString("longitude", "0"));
		ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
		advParseObj.put("locationGeo", point);
		
		// Put USER data on Parse Object
		List<String> coUserIdsList = Arrays.asList(advSettingPref.getString
				("coUserIds", "null").split("\\s*,\\s*"));
		advParseObj.put("coUserIds", coUserIdsList);
		List<String> viUserIdsList = Arrays.asList(advSettingPref.getString
				("viUserIds", "null").split("\\s*,\\s*"));
		advParseObj.put("viUserIds", viUserIdsList);
		
		// Put FLAGs data on Parse Object
		advParseObj.put("timeFlag", advSettingPref.getBoolean("timeFlag", false));
		advParseObj.put("locationFlag", advSettingPref.getBoolean("locationFlag", false));
		advParseObj.put("coUserFlag", advSettingPref.getBoolean("coUserFlag", false));
		advParseObj.put("viUserFlag", advSettingPref.getBoolean("viUserFlag", false));
		
		advParseObj.saveEventually();
		
		advSettingPref.edit().clear().commit();
		initialiazeFlags();
		
		Toast.makeText(getActivity().getApplicationContext(), "Info: Your data has been successfully stored on the server", 
        								Toast.LENGTH_SHORT).show();
//		        	}
//		        } else {
//		            Log.d("ParseError", "Error: " + e.getMessage());
//		        }
//			}
//		});
	}

	private String fetchUserInfo(String type) {
		
		SharedPreferences userInfoPref = getActivity().getSharedPreferences(USER_INFO_PREFS, 0);
		String userInfo = userInfoPref.getString(type, "None");
		
		return userInfo;
	}
	
	private void initialiazeFlags() {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    editor.putBoolean("timeFlag" , false);
	    editor.putBoolean("locationFlag" , false);
	    editor.putBoolean("coUserFlag" , false);
	    editor.putBoolean("viUserFlag" , false);
		editor.commit();
	}
	
	private Boolean checkFalseFlags() {
		
		Boolean falseAllFlags = false;
		SharedPreferences advSettingPref = getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
		Boolean timeFlag = advSettingPref.getBoolean("timeFlag", true);
		Boolean locationFlag = advSettingPref.getBoolean("locationFlag", true);
		Boolean coUserFlag = advSettingPref.getBoolean("coUserFlag", true);
		Boolean viUserFlag = advSettingPref.getBoolean("viUserFlag", true);
		if (!timeFlag && !locationFlag && !coUserFlag && !viUserFlag) {
			falseAllFlags = true;
		}
		return falseAllFlags;
	}
}
