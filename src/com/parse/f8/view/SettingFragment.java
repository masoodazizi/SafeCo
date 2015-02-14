package com.parse.f8.view;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.f8.R;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class SettingFragment extends Fragment {
	
	public static final String USER_INFO_PREFS = "UserInfoPrefs";

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
	
	private String userId;
	
	public SettingFragment() {
		// Required empty public constructor
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

		userId = fetchUserInfo("fbId");
	    View settingView = inflater.inflate(R.layout.fragment_setting, container, false);
	    
	    Bundle args = getArguments();
	    if (args != null && args.containsKey("key")) {
	        String key = args.getString("key");
	        Log.d("Bundle", key);
	    }
	    
	    radioGroupSimplePrivacy = (RadioGroup) settingView.findViewById(R.id.rgroup_simpleprivacy);
		radioGroupCustomIdentity = (RadioGroup) settingView.findViewById(R.id.rgroup_customprivacy_identity);
		radioGroupCustomTime = (RadioGroup) settingView.findViewById(R.id.rgroup_customprivacy_time);
		radioGroupCustomLocation = (RadioGroup) settingView.findViewById(R.id.rgroup_customprivacy_location);
		
	    simplePrivacyListener();
	
	    return settingView;
	}
	
	private void simplePrivacyListener() {
		
//		final View settingView = view;		
		radioGroupSimplePrivacy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
//				rbtnNormal = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_normal);
//				rbtnFair = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_fair);
//				rbtnStrict = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_strict);
//				rbtnFull = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_full);
//				rbtnCustom = (RadioButton) settingView.findViewById(R.id.rbtn_simpleprivacy_custom);
				
				switch (checkedId) {
		        case -1:
		          Log.d("MyDebug", "Choices cleared!");
		          enableCustomRadioGroups(false);
		          break;
		        case R.id.rbtn_simpleprivacy_normal:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_normal");
		          enableCustomRadioGroups(false);
		          savePrivacyProfileDataToParse("identityLvl", 0);
		          savePrivacyProfileDataToParse("timeLvl", 0);
		          savePrivacyProfileDataToParse("locationLvl", 0);
		          break;
		        case R.id.rbtn_simpleprivacy_fair:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_fair");
		          enableCustomRadioGroups(false);
		          savePrivacyProfileDataToParse("identityLvl", 0);
		          savePrivacyProfileDataToParse("timeLvl", 1);
		          savePrivacyProfileDataToParse("locationLvl", 1);
		          break;
		        case R.id.rbtn_simpleprivacy_strict:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_strict");
		          enableCustomRadioGroups(false);
		          savePrivacyProfileDataToParse("identityLvl", 1);
		          savePrivacyProfileDataToParse("timeLvl", 2);
		          savePrivacyProfileDataToParse("locationLvl", 2);
		          break;
		        case R.id.rbtn_simpleprivacy_full:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_full");
		          enableCustomRadioGroups(false);
		          savePrivacyProfileDataToParse("identityLvl", 2);
		          savePrivacyProfileDataToParse("timeLvl", 1);
		          savePrivacyProfileDataToParse("locationLvl", 1);
		          break;
		        case R.id.rbtn_simpleprivacy_custom:
		          Log.d("MyDebug", "Chose rbtn_simpleprivacy_custom");
		          enableCustomRadioGroups(true);
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
		          savePrivacyProfileDataToParse("identityLvl", 0);
		          break;
		        case R.id.rbtn_customprivacy_identity_medium:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_identity_medium");
		          savePrivacyProfileDataToParse("identityLvl", 1);
		          break;
		        case R.id.rbtn_customprivacy_identity_high:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_identity_high");
		          savePrivacyProfileDataToParse("identityLvl", 2);
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
		          savePrivacyProfileDataToParse("timeLvl", 0);
		          break;
		        case R.id.rbtn_customprivacy_time_medium:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_time_medium");
		          savePrivacyProfileDataToParse("timeLvl", 1);
		          break;
		        case R.id.rbtn_customprivacy_time_high:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_time_high");
		          savePrivacyProfileDataToParse("timeLvl", 2);
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
		          savePrivacyProfileDataToParse("locationLvl", 0);
		          break;
		        case R.id.rbtn_customprivacy_location_medium:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_location_medium");
		          savePrivacyProfileDataToParse("locationLvl", 1);
		          break;
		        case R.id.rbtn_customprivacy_location_high:
		          Log.d("MyDebug", "Chose rbtn_customprivacy_location_high");
		          savePrivacyProfileDataToParse("locationLvl", 2);
		          break;
		        default:
		          Log.d("MyDebug", "Nothing!");
		          break;
		        }
			}
		});
	}
	
	private void savePrivacyProfileDataToParse(final String type, final int value) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("PrivacyProfile");
		query.whereEqualTo("userId", userId);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {
				
				// TASK Initialize the user field in PrivacyProfile class of Parse to create related row
				// Check could it be better or not
		        if (e == null) {
		        	if (userObj.size()==0) {
		        		
		        		ParseObject parseObj = new ParseObject("PrivacyProfile");
		        		parseObj.put("userId", userId);
		        		parseObj.put(type, value);
		        		parseObj.saveInBackground();
		        	}
		        	
		        	else if (userObj.size()==1) {
		        		
		        		ParseObject user = userObj.get(0);
		        		user.put(type, value);
		        		user.saveInBackground();
		        	}
		        	
		        	else {
		        		Log.d("ParseError", "More than one userID exists!");
		        		Toast.makeText(getActivity().getApplicationContext(), 
		        				"Error! There are multiple user stored with your profile",
		        				   Toast.LENGTH_LONG).show();
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
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	Bitmap mIcon_val = null;
//	try {
//		mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
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
//		// TODO Auto-generated method stub
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

