package com.parse.f8.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.f8.R;
import com.parse.f8.R.id;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class SettingFragment extends Fragment {
	
	public String prefs = "UserInfoPrefs";
	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	public static final String ADV_SETTING_PREFS = "AdvSettingPrefs";
	public static final String PARSE_SIMPLE_PRIVACY_CLASS = "PrivacyProfile";
	
	private RadioGroup radioGroupSimplePrivacy;
	private RadioGroup radioGroupCustomIdentity;
	private RadioGroup radioGroupCustomTime;
	private RadioGroup radioGroupCustomLocation;
	private RadioButton rbtnNormal;
	private RadioButton rbtnFair;
	private RadioButton rbtnStrict;
	private RadioButton rbtnFull;
	private RadioButton rbtnCustom;
	private RadioButton rbtnIdLow;
	private RadioButton rbtnIdMed;
	private RadioButton rbtnIdHigh;
	private RadioButton rbtnTimeLow;
	private RadioButton rbtnTimeMed;
	private RadioButton rbtnTimeHigh;
	private RadioButton rbtnLocLow;
	private RadioButton rbtnLocMed;
	private RadioButton rbtnLocHigh;
	private TextView btnAdvSetting;
	private String userId;
	private String key;
	private String parseClass;
	
	public SettingFragment() {
		// Required empty public constructor
		
	}
	// FIXMED Create subtabs for simple and advanced privacy settings // replaced by an option bar in bottom
	// TASK when a profile is selected (except setting), the custom values change accordingly
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

		userId = fetchUserInfo("fbId");
	    View settingView = inflater.inflate(R.layout.fragment_setting, container, false);
	    
	    radioGroupSimplePrivacy = (RadioGroup) settingView.findViewById(R.id.rgroup_simpleprivacy);
		radioGroupCustomIdentity = (RadioGroup) settingView.findViewById(R.id.rgroup_customprivacy_identity);
		radioGroupCustomTime = (RadioGroup) settingView.findViewById(R.id.rgroup_customprivacy_time);
		radioGroupCustomLocation = (RadioGroup) settingView.findViewById(R.id.rgroup_customprivacy_location);
		rbtnNormal = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_normal);
		rbtnFair = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_fair);
		rbtnStrict = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_strict);
		rbtnFull = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_full);
		rbtnCustom = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_custom);
		rbtnIdLow = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_identity_low);
		rbtnIdMed = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_identity_medium);
		rbtnIdHigh = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_identity_high);
		rbtnTimeLow = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_time_low);
		rbtnTimeMed = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_time_medium);
		rbtnTimeHigh = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_time_high);
		rbtnLocLow = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_location_low);
		rbtnLocMed= (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_location_medium);
		rbtnLocHigh = (RadioButton) settingView.findViewById(R.id.rbtn_customprivacy_location_high);
		
//		rbtnCustom = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_custom);
		if (!rbtnCustom.isChecked()) {
			enableCustomRadioGroups(false);
		}
		
	    Bundle args = getArguments();
	    if (args != null && args.containsKey("key")) {
	        key = args.getString("key");
	        Log.d("Bundle", key);
	    }
	    
	    if (key == "add") {
	    	prefs = "AdvSettingPrefs";
	    	parseClass = "RestrictedList";
	    	initializeAdvHeader(settingView);
	    	onSwitchClicked(settingView);
	    }
	    else {
	    	prefs = "SimplePrivacyPrefs";
	    	parseClass = "PrivacyProfile";
	    	setAdvSettingButton(settingView);
	    	initializePrivacyProfile();
	    }
	    		
	    simplePrivacyListener();
	    
	    return settingView;
	}
	
	private void simplePrivacyListener() {
		
//		final View settingView = view;		
		radioGroupSimplePrivacy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (checkedId) {
		        case -1:
		          Log.d("MyDebug", "Choices cleared!");
		          enableCustomRadioGroups(false);
		          break;
		        case R.id.rbtn_simpleprivacy_normal:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_normal");
		          rbtnIdLow.setChecked(true);
		          rbtnTimeLow.setChecked(true);
		          rbtnLocLow.setChecked(true);
		          enableCustomRadioGroups(false);
		          savePrivacyProfileData("identityLvl", 0);
		          savePrivacyProfileData("timeLvl", 0);
		          savePrivacyProfileData("locationLvl", 0);
		          savePrivacyProfileToParse("normal");
		          break;
		        case R.id.rbtn_simpleprivacy_fair:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_fair");
		          rbtnIdLow.setChecked(true);
		          rbtnTimeMed.setChecked(true);
		          rbtnLocMed.setChecked(true);
		          enableCustomRadioGroups(false);
		          savePrivacyProfileData("identityLvl", 0);
		          savePrivacyProfileData("timeLvl", 1);
		          savePrivacyProfileData("locationLvl", 1);
		          savePrivacyProfileToParse("fair");
		          break;
		        case R.id.rbtn_simpleprivacy_strict:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_strict");
		          rbtnIdMed.setChecked(true);
		          rbtnTimeHigh.setChecked(true);
		          rbtnLocHigh.setChecked(true);
		          enableCustomRadioGroups(false);
		          savePrivacyProfileData("identityLvl", 1);
		          savePrivacyProfileData("timeLvl", 2);
		          savePrivacyProfileData("locationLvl", 2);
		          savePrivacyProfileToParse("strict");
		          break;
		        case R.id.rbtn_simpleprivacy_full:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_full");
		          rbtnIdHigh.setChecked(true);
		          rbtnTimeMed.setChecked(true);
		          rbtnLocMed.setChecked(true);
		          enableCustomRadioGroups(false);
		          savePrivacyProfileData("identityLvl", 2);
		          savePrivacyProfileData("timeLvl", 1);
		          savePrivacyProfileData("locationLvl", 1);
		          savePrivacyProfileToParse("full");
		          break;
		        case R.id.rbtn_simpleprivacy_custom:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_custom");
		          rbtnIdLow.setChecked(true);
		          rbtnTimeLow.setChecked(true);
		          rbtnLocLow.setChecked(true);
		          enableCustomRadioGroups(true);
		          savePrivacyProfileToParse("custom");
		          customPrivacyListener();
		          break;
		        default:
		          Log.d("MyDebug", "Nothing!");
		          break;
		        }
			}
		});
	}
	
	private void enableCustomRadioGroups(Boolean enable) {
		
		enableRadioGroup(radioGroupCustomIdentity, enable);
		enableRadioGroup(radioGroupCustomTime, enable);
		enableRadioGroup(radioGroupCustomLocation, enable);		
	}
	
	private void enableRadioGroup(RadioGroup radioGroup, Boolean enable) {
		
        for(int i = 0; i < radioGroup.getChildCount(); i++){
            ((RadioButton)radioGroup.getChildAt(i)).setEnabled(enable);
        }
	}
	
	private void customPrivacyListener() {
		
//		final View settingView = view;
		
		radioGroupCustomIdentity.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (checkedId) {
		        case -1:
		          Log.d("MyDebug", "Choices cleared!");
		          break;
		        case R.id.rbtn_customprivacy_identity_low:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_identity_low");
		          savePrivacyProfileData("identityLvl", 0);
		          break;
		        case R.id.rbtn_customprivacy_identity_medium:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_identity_medium");
		          savePrivacyProfileData("identityLvl", 1);
		          break;
		        case R.id.rbtn_customprivacy_identity_high:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_identity_high");
		          savePrivacyProfileData("identityLvl", 2);
		          break;
		        default:
		          Log.d("MyDebug", "Nothing!");
		          break;
		        }
			}
		});
		
		radioGroupCustomTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (checkedId) {
		        case -1:
		          Log.d("MyDebug", "Choices cleared!");
		          break;
		        case R.id.rbtn_customprivacy_time_low:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_time_low");
		          savePrivacyProfileData("timeLvl", 0);
		          break;
		        case R.id.rbtn_customprivacy_time_medium:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_time_medium");
		          savePrivacyProfileData("timeLvl", 1);
		          break;
		        case R.id.rbtn_customprivacy_time_high:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_time_high");
		          savePrivacyProfileData("timeLvl", 2);
		          break;
		        default:
		          Log.d("MyDebug", "Nothing!");
		          break;
		        }
			}
		});
		
		radioGroupCustomLocation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (checkedId) {
		        case -1:
		          Log.d("MyDebug", "Choices cleared!");
		          break;
		        case R.id.rbtn_customprivacy_location_low:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_location_low");
		          savePrivacyProfileData("locationLvl", 0);
		          break;
		        case R.id.rbtn_customprivacy_location_medium:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_location_medium");
		          savePrivacyProfileData("locationLvl", 1);
		          break;
		        case R.id.rbtn_customprivacy_location_high:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_location_high");
		          savePrivacyProfileData("locationLvl", 2);
		          break;
		        default:
		          Log.d("MyDebug", "Nothing!");
		          break;
		        }
			}
		});
	}
	
	private void savePrivacyProfileData(final String type, final int value) {
		
		if (key == "add") {
	    	savePrivacyProfileDataToPrefs(type, value);
	    }
	    
	    else {
	    	savePrivacyProfileDataToParse(type, value);
	    }
	}
	
	private void savePrivacyProfileDataToPrefs(final String type, final int value) {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(prefs, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    editor.putInt(type, value);
		editor.commit();
	}
	
	private void savePrivacyProfileDataToParse(final String type, final int value) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_SIMPLE_PRIVACY_CLASS);
		query.whereEqualTo("userId", userId);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {
				
				// TASK Initialize the user field in PrivacyProfile class of Parse to create related row
				// Check could it be better or not
		        if (e == null) {
//		        	if (userObj.size()==0) {
//		        		
//		        		ParseObject parseObj = new ParseObject("PrivacyProfile");
//		        		parseObj.put("userId", userId);
//		        		parseObj.put(type, value);
//		        		parseObj.saveInBackground();
//		        	}
//		        	
//		        	else if (userObj.size()==1) {
		        		
		        		ParseObject user = userObj.get(0);
		        		user.put(type, value);
		        		user.saveInBackground();
//		        	}
//		        	
//		        	else {
//		        		Log.d("ParseError", "More than one userID exists!");
//		        		Toast.makeText(getActivity().getApplicationContext(), 
//		        				"Error! There are multiple user stored with your profile",
//		        				   Toast.LENGTH_LONG).show();
//		        	}
		            
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
	
	private void setAdvSettingButton(View v) {
		
	    btnAdvSetting = (TextView) v.findViewById(R.id.txtbtn_advsetting);
	    btnAdvSetting.setEnabled(true);
	    btnAdvSetting.setVisibility(View.VISIBLE);
	    btnAdvSetting.setOnTouchListener(new OnTouchListener() {
			
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				Fragment newFragment = new SettingAdvMain();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
			    transaction.replace(R.id.fragment_setting_layout, newFragment);
			    transaction.addToBackStack(null);
			    transaction.setTransition(4099);
			    transaction.commit(); 
				return false;
			}
		});
	}
	
	private void initializePrivacyProfile() {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_SIMPLE_PRIVACY_CLASS);
		query.whereEqualTo("userId", userId);
		query.findInBackground(new FindCallback<ParseObject>() {
		// CHECK It might be faster, if parse data is loaded in MainActivity and stored in a shared preferences, then loaded to widgets here	
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {

		        if (e == null) {
		        	
		        		ParseObject user = userObj.get(0);
		        		switch (user.getString("profile")) {
						case "normal":
							rbtnNormal.setChecked(true);
							break;
						case "fair":
							rbtnFair.setChecked(true);
							break;
						case "strict":
							rbtnStrict.setChecked(true);
							break;
						case "full":
							rbtnFull.setChecked(true);
							break;
						case "custom":
							rbtnCustom.setChecked(true);
							switch (user.getInt("identityLvl")) {
							case 0:
								rbtnIdLow.setChecked(true);
								break;
							case 1:
								rbtnIdMed.setChecked(true);
								break;
							case 2:
								rbtnIdHigh.setChecked(true);
								break;
							default:
								break;
							}
							switch (user.getInt("timeLvl")) {
							case 0:
								rbtnTimeLow.setChecked(true);
								break;
							case 1:
								rbtnTimeMed.setChecked(true);
								break;
							case 2:
								rbtnTimeHigh.setChecked(true);
								break;
							default:
								break;
							}
							switch (user.getInt("locationLvl")) {
							case 0:
								rbtnLocLow.setChecked(true);
								break;
							case 1:
								rbtnLocMed.setChecked(true);
								break;
							case 2:
								rbtnLocHigh.setChecked(true);
								break;
							default:
								break;
							}
							break;

						default:
							break;
						}
		        	}
		            
		        else {
		            Log.d("ParseError", "Error: " + e.getMessage());
		        }
			}
		});
	}
	
	private void initializeAdvHeader(View v) {
		
		TextView txtRestrictedList = (TextView) v.findViewById(R.id.lbl_profileRestrictedList);
		txtRestrictedList.setVisibility(View.VISIBLE);
		View line = (View) v.findViewById(R.id.line_privacyProfile);
		line.setVisibility(View.VISIBLE);
		Switch switchPrivacyProfile = (Switch) v.findViewById(R.id.switch_privacyProfile);
		switchPrivacyProfile.setVisibility(View.VISIBLE);
	}
	
	private void savePrivacyProfileToParse(final String value) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_SIMPLE_PRIVACY_CLASS);
		query.whereEqualTo("userId", userId);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {

		        if (e == null) {
		        		ParseObject user = userObj.get(0);
		        		user.put("profile", value);
		        		user.saveInBackground();

		        } else {
		            Log.d("ParseError", "Error: " + e.getMessage());
		        }
			}
		});

	}
	
	public void onSwitchClicked(final View v) {
		
		Switch disableSwitch = (Switch) v.findViewById(R.id.switch_privacyProfile);
		disableSwitch.setChecked(true);
		disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked) {
					enableRadioGroup(radioGroupSimplePrivacy, true);		
				}
				else {
					rbtnNormal.setChecked(true);
					rbtnIdLow.setChecked(true);
					rbtnTimeLow.setChecked(true);
					rbtnLocLow.setChecked(true);
					enableRadioGroup(radioGroupSimplePrivacy, false);
					enableCustomRadioGroups(false);
					removePrefsKeys();
				}
			}
		});

	}
	
	private void removePrefsKeys() {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
	    SharedPreferences.Editor editor = advSettingPref.edit();
	    editor.remove("locationLvl");
	    editor.remove("timeLvl");
	    editor.remove("identityLvl");
		editor.commit();
	}

}
	

//public static ProfilePictureView profilePictureView;
//public static TextView userNameView;
//private TextView txt;
//public static final String MY_PREFS_NAME = "MyPrefsFile";

//	profilePictureView = (ProfilePictureView) settingView.findViewById(R.id.profile_pic);
//	profilePictureView.setCropped(true);
//    userNameView = (TextView) settingView.findViewById(R.id.profile_username);
//
//    txt = (TextView) settingView.findViewById(R.id.txtName);
//
//    
//    SharedPreferences settings = this.getActivity().getSharedPreferences(MY_PREFS_NAME, 0);
//    String highScore = settings.getString("name", "No name defined");
//	txt.setText(highScore);
    
//	String imgPath = settings.getString("imgUrl", null);
//	URL newurl = null;
//	try {
//		newurl = new URL(imgPath);
//	} catch (MalformedURLException e) {
//		e.printStackTrace();
//	}
//	Bitmap mIcon_val = null;
//	try {
//		mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//	} catch (IOException e) {
//		e.printStackTrace();
//	} 
//	ImageView profilePhoto = (ImageView) settingView.findViewById(R.id.profile_photo);
//	profilePhoto.setImageBitmap(mIcon_val);
//	
	
	
	
	
	
//    txt.setText("Masood Azizi");
    
//    SharedPreferences sharedPref = getActivity().getPreferences(getActivity().getApplicationContext().MODE_PRIVATE);

    
//    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE); 
//    String restoredText = prefs.getString("text", null);
//    if (restoredText != null) {
//      String highscore = prefs.getString("name", "No name defined");//"No name defined" is the default value.
//      int idName = prefs.getInt("idName", 0); //0 is the default value.
//    }
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// Inflate the layout for this fragment
//		return inflater.inflate(R.layout.fragment_setting, container, false);
//	}
//	
//	@Override
//	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		
//		profilePictureView = (ProfilePictureView) getActivity().findViewById(R.id.profile_pic);
//		profilePictureView.setCropped(true);
//		userNameView = (TextView) getActivity().findViewById(R.id.profile_username);
//				
//		txt = (TextView) rootView.findViewById(R.id.txtName);
//		txt.setText("Masood Azizi");
//		SharedPreferences sharedPref = getActivity().getPreferences(getActivity().getApplicationContext().MODE_PRIVATE);
//		
//		String highScore = sharedPref.getString("name", "");
//		txt.setText(highScore);
//	}

