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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
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
	RadioGroup rGroupAdvUser;
	RadioButton rbtnEveryone;
	ArrayList<String> friendsList = new ArrayList<String>();
	String key;
	String advUserTitleText;
	String advCoUserSelectText;
	ArrayAdapter<String> adapter;
	
	public SettingAdvCoUser() {
		// Required empty public constructor
	}
	// TASK Add radiobutton to choose everyone!
	
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
	    
	    rbtnEveryone = (RadioButton) advCoUserView.findViewById(R.id.rbutton_everyone);
	    advUserTitle = (TextView) advCoUserView.findViewById(R.id.lbl_advuser);
	    advUserTitle.setText(advUserTitleText);
	    listViewFriends = (ListView) advCoUserView.findViewById(R.id.list_cousers_added);
	    advCoUserSelect = (TextView) advCoUserView.findViewById(R.id.lbl_advCoUserSelect);
	    advCoUserSelect.setText(advCoUserSelectText);
	    txtCoUserAdd = (EditText) advCoUserView.findViewById(R.id.txt_couser_enter);
		btnFriendPicker = (Button) advCoUserView.findViewById(R.id.btn_add_couser);
		
		onSwitchClicked(advCoUserView);
		
		rGroupAdvUser = (RadioGroup) advCoUserView.findViewById(R.id.radioGroup_advUser);
		rGroupAdvUser.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				if (checkedId == R.id.rbutton_everyone) {
					
					String friendsType = "noUser";
				    if (key == "coUser") {
				    	friendsType = "coUserIds";
				    }
				    
				    else if (key == "viUser") {
				    	friendsType = "viUserIds";
				    }
//					saveAdvSettingPref(friendsType, "$everyone");
					
					txtCoUserAdd.setEnabled(false);
					btnFriendPicker.setEnabled(false);
					if (adapter != null) {
						adapter.clear();
						listViewFriends.setVisibility(View.INVISIBLE);
					}
				}
				else if (checkedId == R.id.rbutton_selective) {
					
					txtCoUserAdd.setEnabled(true);
					btnFriendPicker.setEnabled(true);
				}
			}
		});
		
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
				
				String friendName = txtCoUserAdd.getText().toString();
				if (friendName != null) {
					friendsList.add(friendName);
				}
				
				listViewFriends.setVisibility(View.VISIBLE);
				adapter = new ArrayAdapter<String>(getActivity(), 
						android.R.layout.simple_list_item_1, friendsList);
				listViewFriends.setAdapter(adapter);
				
				String friendsType = "noUser";
			    if (key == "coUser") {
			    	friendsType = "coUserIds";
			    }
			    
			    else if (key == "viUser") {
			    	friendsType = "viUserIds";
			    }
				saveAdvSettingArrayPref(friendsType, friendsList);
				
				txtCoUserAdd.setText("");
			}
		});
		
		return advCoUserView;
	}
	
	
	private void saveAdvSettingArrayPref(String type, ArrayList<String> values) {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    
//	    JSONArray a = new JSONArray();
//	    for (int i = 0; i < values.size(); i++) {
//	        a.put(values.get(i));
//	    }
//	    if (!values.isEmpty()) {
//	        editor.putString(type, a.toString());
//	    } else {
//	    	 editor.putString(type, null);
//	    }
	    
	    String userString = null;
	    
	    for (String item : values) {
	    	if (userString == null) {
	    		userString = item;
	    	} else {
	    		userString = userString + " , " + item;
	    	}
	    }
	    editor.putString(type , userString);
	    
	    if (type == "coUserIds") {
	    	editor.putBoolean("coUserFlag", true);
	    }
	    else if(type == "viUserIds") {
	    	editor.putBoolean("viUserFlag", true);
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
	
	public void onSwitchClicked(final View v) {
		
		Switch disableSwitch = (Switch) v.findViewById(R.id.switch_user);
		disableSwitch.setChecked(true);
		disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					enableWidgets(true);		
				}
				else {
					
					enableWidgets(false);
					removePrefsKeys();
				}
			}
		});

	}

	private void enableWidgets(Boolean enable) {
		
		txtCoUserAdd.setText("");
		txtCoUserAdd.setEnabled(enable);
		btnFriendPicker.setEnabled(enable);
		if (!enable && (adapter != null)) {
			adapter.clear();
			listViewFriends.setVisibility(View.INVISIBLE);
		}
		for(int i = 0; i < rGroupAdvUser.getChildCount(); i++){
            ((RadioButton)rGroupAdvUser.getChildAt(i)).setEnabled(enable);
        }
		rbtnEveryone.setChecked(true);
//        if (!enable) {
//        	rGroupAdvUser.clearCheck();
//        }
	}
	
	private void removePrefsKeys() {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    if (key == "coUser") {
	    	editor.remove("coUserIds");
	    	editor.putBoolean("coUserFlag", false);
	    }
	    
	    else if (key == "viUser") {
	    	editor.remove("viUserIds");
	    	editor.putBoolean("viUserFlag", false);
	    }
	    
		editor.commit();
	}
	
//	private void saveAdvSettingPref(String type, String value) {
//		
//		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
//	    SharedPreferences.Editor editor = advSettingPref.edit();
//	    editor.putString(type , value);
//		editor.commit();
//		
//	}
}



