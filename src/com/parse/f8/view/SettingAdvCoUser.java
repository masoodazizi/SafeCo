package com.parse.f8.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class SettingAdvCoUser extends Fragment {

	public static final String ADV_SETTING_PREFS = "AdvSettingPrefs";
	Button btnFriendPicker;
	EditText txtCoUserAdd;
	ListView listViewFriends;
	TextView advUserTitle;
	TextView advCoUserSelect;
	ArrayList<String> friendsList = new ArrayList<String>();
	String key;
	String advUserTitleText;
	String advCoUserSelectText;
	
	public SettingAdvCoUser() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View advCoUserView = inflater.inflate(R.layout.setting_advcouser, container, false);
		
	    Bundle args = getArguments();
	    if (args != null && args.containsKey("key")) {
	        key = args.getString("key");
	        Log.d("Bundle", key);
	    }
	    
	    	    
	    if (key == "coUser") {
	    	advUserTitleText = "Co-User";
	    	advCoUserSelectText = "Select the users must not have co-location with you:";
	    }
	    
	    else if (key == "viUser") {
	    	advCoUserSelectText = "Select the users must not view co-location posts you are tagged in:";
	    	advUserTitleText = "View-User";
	    }
	    
	    advUserTitle = (TextView) advCoUserView.findViewById(R.id.lbl_advuser);
	    advUserTitle.setText(advUserTitleText);
	    
	    advCoUserSelect = (TextView) advCoUserView.findViewById(R.id.lbl_advCoUserSelect);
	    advCoUserSelect.setText(advCoUserSelectText);
		
		btnFriendPicker = (Button) advCoUserView.findViewById(R.id.btn_add_couser);
		btnFriendPicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// FIXME FriendPickFragment is empty! fix or try another solution. Try to use the activity class instead of fragment
				// TASK After friend list worked, get the userFbIds and replace it in Shared Prefs Value.
				
//				Fragment fragment = new FBPickerFragment();
//			    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//			    transaction.replace(R.id.fragment_couser, fragment);
//			    transaction.addToBackStack(null);
//			    transaction.setTransition(4099);	
//			    transaction.commit(); 
			   
				
				txtCoUserAdd = (EditText) advCoUserView.findViewById(R.id.txt_couser_enter);
				String friendName = txtCoUserAdd.getText().toString();
				if (friendName != null) {
					friendsList.add(friendName);
				}
				
				listViewFriends = (ListView) advCoUserView.findViewById(R.id.list_cousers_added);
				listViewFriends.setVisibility(0);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
						android.R.layout.simple_list_item_1, friendsList);
				listViewFriends.setAdapter(adapter);
				
				String friendsType = "noUser";
			    if (key == "coUser") {
			    	friendsType = "coUserIds";
			    }
			    
			    else if (key == "viUser") {
			    	friendsType = "viUserIds";
			    }
				saveAdvSettingPref(friendsType, friendsList);
				
				txtCoUserAdd.setText("");
			}
		});
		
		return advCoUserView;
	}
	
	
	private void saveAdvSettingPref(String type, ArrayList<String> values) {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    
	    JSONArray a = new JSONArray();
	    for (int i = 0; i < values.size(); i++) {
	        a.put(values.get(i));
	    }
	    if (!values.isEmpty()) {
	        editor.putString(type, a.toString());
	    } else {
	    	 editor.putString(type, null);
	    }
	       
		editor.commit();
		
	}


	public ArrayList<String> restoreAdvSettingPref(String type) {
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    String json = advSettingPref.getString(type, null);
	    ArrayList<String> friendsList = new ArrayList<String>();
	    if (json != null) {
	        try {
	            JSONArray a = new JSONArray(json);
	            for (int i = 0; i < a.length(); i++) {
	                String friendName = a.optString(i);
	                friendsList.add(friendName);
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	    }
	    return friendsList;
	}
	
}
