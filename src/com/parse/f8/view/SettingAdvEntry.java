package com.parse.f8.view;

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
// FIXME widgets from background fragment work on the foreground active fragment!

public class SettingAdvEntry extends Fragment {

	public static final String ADV_SETTING_PREFS = "AdvSettingPrefs";
	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	public static final String PARSE_ADV_PRIVACY_CLASS = "RestrictedList";
	String[] advEntryListItems;
	Button buttonAddEntry;
	private String userId;
	
	
	public SettingAdvEntry() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View advEntryView = inflater.inflate(R.layout.setting_adventry, container, false);
		userId = fetchUserInfo("fbId");
		
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
		
		final SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_ADV_PRIVACY_CLASS);
		query.whereEqualTo("userId", userId);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {
				
		        if (e == null) {
		        	
		        	if (userObj == null) {
		        		Log.d("ParseQueryError", "There is no user object with user ID " + userId + 
		        				"is defined in <" + PARSE_ADV_PRIVACY_CLASS + "> Parse Class");

		        	} else {
		        		ParseObject user = userObj.get(0);
		        		user.put("identityLvl", advSettingPref.getInt("identityLvl", 0));
		        		user.put("timeLvl", advSettingPref.getInt("timeLvl", 0));
		        		user.put("locationLvl", advSettingPref.getInt("locationLvl", 0));
		        		user.put("dayOfWeek", advSettingPref.getInt("dayOfWeek", 0));
		        		user.put("timePeriod", advSettingPref.getString("timePeriod", "null"));
		        		user.put("timeStart2", advSettingPref.getString("timeStart", "null"));
		        		user.put("timeEnd2", advSettingPref.getString("timeEnd", "null"));
		        		user.put("timeDayPart", advSettingPref.getString("timeDayPart", "null"));
		        		user.put("locationAddr", advSettingPref.getString("locationAddr", "null"));
		        		
		        		double latitude = Double.parseDouble(advSettingPref.getString("latitude", "0"));
		        		double longitude = Double.parseDouble(advSettingPref.getString("longitude", "0"));
		        		ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
		        		user.put("locationGeo", point);
		        		user.saveEventually();
		        	}
		        } else {
		            Log.d("ParseError", "Error: " + e.getMessage());
		        }
			}
		});
	}

	private String fetchUserInfo(String type) {
		
		SharedPreferences userInfoPref = getActivity().getSharedPreferences(USER_INFO_PREFS, 0);
		String userInfo = userInfoPref.getString(type, "None");
		
		return userInfo;
	}
}
