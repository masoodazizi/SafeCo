package com.parse.f8.view;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.f8.AddressConverter;
import com.parse.f8.R;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class ProfileFragment extends Fragment {

	public static final String PROFILE_PIC_PREF = "profilePicPrefs";
	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	public static final String STATUS_UPDATE_PREFS = "statusUpdatePrefs";
	
	private TextView text_current_time;
	private EditText text_status;
	private EditText text_location;
	private EditText txtFriendList;
	private ImageView imageMapPin;
	private View datePicker;
	private ImageView timePicker;
	private String latitude = null;
	private String longitude = null;
	Calendar calendar;
	ParseGeoPoint locationGeo;
	String locationL0;
	String locationL1;
	String locationL2;
	
	
	public ProfileFragment() {
		
	}
	// TASK Add news feed list of users post to his own profile with removing feature
	// TASK Add selecting users from facebook list
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
//		Log.d("DEBUG", "fragment hint setting..."+isVisibleToUser);
		if (isVisibleToUser) {
			if (fetchPathPref() == null) {
				setProfilePhoto();
			}
		}
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
		
		// Fetch profile photo path and load it to ImageView
		
		setProfileInfo(profileView);
		removeLocPrefsKeys();
		
		// Location info initialization
		txtFriendList = (EditText) getActivity().findViewById(R.id.txt_with);
		text_location = (EditText) profileView.findViewById(R.id.txt_location);
		setCurrentLocation();
		
		// Time info initialization
		text_current_time = (TextView) profileView.findViewById(R.id.txt_time);
		calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		calendar.setTimeZone(TimeZone.getTimeZone("CET"));
		printDate();
		

		
		
		imageMapPin = (ImageView) profileView.findViewById(R.id.image_map_pin);
		imageMapPin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Fragment newFragment = new GoogleMapFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
			    transaction.replace(R.id.profile_frameLayout, newFragment);
			    transaction.addToBackStack(null);
			    transaction.setTransition(4099);	
			    transaction.commit(); 
			}
		});
		
		datePicker = profileView.findViewById(R.id.view_date_picker);
		datePicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				onDatePickerClicked();
			}
		});
		
		timePicker = (ImageView) profileView.findViewById(R.id.image_time_picker);
		timePicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				onTimePickerClicked();
			}
		});
		
		Button postButton = (Button) profileView.findViewById(R.id.btn_post);
		postButton.setOnClickListener(new  View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				onPostButtonClicked(v);
			}
		});
		
		FragmentManager fm = getFragmentManager();
		fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {
				
				Log.d("MyDebug", "Backstack changed");
				readLocDataFromPrefs();
				if (latitude != null && latitude != "null" && longitude != null && longitude != "null") {
					
					double lat = Double.parseDouble(latitude);
					double lng = Double.parseDouble(longitude);
					String address = "Address not fetched";
					AddressConverter addressConvertor = new AddressConverter
							(getActivity().getApplicationContext(), lat, lng);
					address = addressConvertor.getAddress();
					text_location.setText(address);
					
					locationGeo = new ParseGeoPoint(lat, lng);
					locationL0 = address;
					locationL1 = addressConvertor.generalizeFirstLevel();
					locationL2 = addressConvertor.generalizeSecondLevel();
				}
			}
		});
		
		
		ImageView imgPickfriend = (ImageView) profileView.findViewById(R.id.image_pick_friend);
		imgPickfriend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.setTitle("Information");
				alertDialog.setMessage(getResources().getString(R.string.pick_friend));
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				            dialog.dismiss();
				        }
				    });
				alertDialog.show();
				
			}
		});
		
		return profileView;
	}
	
	
	private void onPostButtonClicked(View v) {

		// Initialize
		ParseObject postObj = new ParseObject("Post");
		String name = fetchUserInfo("name");
		postObj.put("owner", name);
		
		String fbId = fetchUserInfo("fbId");
		postObj.put("userId", fbId);
		
		String gender = fetchUserInfo("gender");
//		postObj.put("gender", gender);
		if (gender == "male") {
			postObj.put("genderMale", true);
		}
		else if (gender == "female") {
			postObj.put("genderMale", false);
		}
		
		// STATUS update
		text_status = (EditText) getActivity().findViewById(R.id.txt_status_text);
		String status = text_status.getText().toString();
		// TASK  This if condition does not work!
		if (status == null || status == "") {
			Toast.makeText(getActivity().getApplicationContext(), "Warning: Please enter the status! Empty text does not make sense.", Toast.LENGTH_SHORT).show();
			return;
		}
		postObj.put("text", status);
		
		
		// FRIEND UPDATE
		txtFriendList = (EditText) getActivity().findViewById(R.id.txt_with);
		String friendListString = txtFriendList.getText().toString();
		if (friendListString != null && friendListString != "") {
			List<String> friendsList = Arrays.asList(friendListString.split("\\s*,\\s*"));
			postObj.put("friends", friendsList);
		}
			
		
		// TIME update
		Date finalTime = new Date();
		finalTime = calendar.getTime();
		postObj.put("time0", finalTime);
		
		SimpleDateFormat sdfL0 = new SimpleDateFormat("dd MMM yyyy  |  HH:mm", Locale.ENGLISH);
		String timeL0 = sdfL0.format(calendar.getTime());
		postObj.put("timeL0", timeL0);
		
		SimpleDateFormat sdfL1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		String timeL1 = sdfL1.format(calendar.getTime());
		timeL1 = getTimeofDay(timeL1);
		postObj.put("timeL1", timeL1);
		
		SimpleDateFormat sdfL2 = new SimpleDateFormat("dd MMM yyyy, EE", Locale.ENGLISH);
		String timeL2 = sdfL2.format(calendar.getTime());
		postObj.put("timeL2", timeL2);
		
		
		// LOCATION UPDATE
		postObj.put("locGeo", locationGeo);
		postObj.put("locL0", locationL0);
		postObj.put("locL1", locationL1);
		postObj.put("locL2", locationL2);
		
		
		// Finalization and posting
		postObj.saveInBackground();
		Toast.makeText(getActivity().getApplicationContext(), "Info: Your status has been successfully posted.", Toast.LENGTH_SHORT).show();
		text_status.setText("");
		txtFriendList.setText("");
		text_current_time.setText("");
		text_location.setText("");
	}

	private String getTimeofDay(String time) {
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		String partOFDay = null;
		if (hour >=6 && hour < 12) {
			partOFDay = "Morning";
		}
		else if (hour >=12 && hour < 18) {
			partOFDay = "Afternoon";
		}
		else if (hour >=18 && hour <= 23 ) {
			partOFDay = "Evening";
		}
		else if (hour >=0 && hour < 6) {
			partOFDay = "Night";
		}
		String timeStr = time + "  |  " + partOFDay;
		return timeStr;
	}

	private String fetchPathPref(){
		
		SharedPreferences imgPathPref = this.getActivity().getSharedPreferences(PROFILE_PIC_PREF, 0);
		String imgPath = imgPathPref.getString("imgPath", null);
		
		return imgPath;
	}
	
	private void loadImageFromStorage(String path, ImageView profile_photo)
	{

	    try {
	        File f=new File(path, "profile.jpg");
	        if(f.exists()) {
		        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
		            
		        profile_photo.setImageBitmap(b);
	        } else {
	        	// Load default avatar
	        	profile_photo.setImageResource(R.drawable.male_avatar);
	        }
	    } 
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }

	}
	
	private void setProfilePhoto(){
		
		ImageView profile_photo=(ImageView) getActivity().findViewById(R.id.profile_photo);
		String imgPath = "";
		imgPath = fetchPathPref();
		if (imgPath != null) {
			loadImageFromStorage(imgPath, profile_photo);
		} else {
			profile_photo.setImageResource(R.drawable.male_avatar);
		}
		
	}
	
	private void setProfileInfo(View profileView) {
		
		TextView user_name_text = (TextView) profileView.findViewById(R.id.txtName);
		String userName = fetchUserInfo("name");
		user_name_text.setText(userName);
		Log.d("MyDebug", "FB ID = " + fetchUserInfo("fbId"));
		//....
	}

	private String fetchUserInfo(String type) {
		
		SharedPreferences userInfoPref = this.getActivity().getSharedPreferences(USER_INFO_PREFS, 0);
		String userInfo = userInfoPref.getString(type, null);
		
		return userInfo;
	}
	
	
	private void readLocDataFromPrefs() {
		
		SharedPreferences statusUpdatePref = this.getActivity().getSharedPreferences(STATUS_UPDATE_PREFS, 0);
		latitude = statusUpdatePref.getString("latitude", null);
		longitude = statusUpdatePref.getString("longitude", null);
	}
	
	private void removeLocPrefsKeys() {
		
		SharedPreferences statusUpdatePref = this.getActivity().getSharedPreferences(STATUS_UPDATE_PREFS, 0);
	    SharedPreferences.Editor editor = statusUpdatePref.edit();
	    editor.remove("latitude");
	    editor.remove("longitude");
		editor.commit();
	}
	
	private void onDatePickerClicked() {
		
//		final Calendar c = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dpd = new DatePickerDialog(getActivity(), 
				new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {

						saveDateInfo(year, monthOfYear, dayOfMonth);
					}
				}, mYear, mMonth, mDay);
		dpd.show();
	}
	
	private void saveDateInfo(int year, int month, int day) {
		
		
	    calendar.set(year, month, day);
	    printDate();
	}
	
	private void saveTimeInfo(int hour, int minute) {
		
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		printDate();
	}
	
	private void printDate() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  |  HH:mm", Locale.ENGLISH);
		String date = sdf.format(calendar.getTime());
		text_current_time.setText(date);
	}
	
	private void onTimePickerClicked() {
		
		// Process to get Current Time
//        final Calendar c = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);
        
        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                            int minute) {

                    	saveTimeInfo(hourOfDay, minute);
	                    }
                }, mHour, mMinute, false);
        tpd.show();
	}
	
	private LatLng getCurrentLocation() {
		
		LatLng latLng = null;
		double latitude = 50.778396;
		double longitude = 6.060989;
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location currentLoc = locationManager.getLastKnownLocation(provider);
		if (currentLoc != null) {
			latitude = currentLoc.getLatitude();
			longitude = currentLoc.getLongitude();
		}
		latLng = new LatLng(latitude, longitude);
		return latLng;
	}
	
	private void setCurrentLocation() {
		
		LatLng currentLocPoint = getCurrentLocation();
		Double locLat = currentLocPoint.latitude;
		Double locLng = currentLocPoint.longitude;

		AddressConverter addressConvertor = new AddressConverter
				(getActivity().getApplicationContext(), locLat, locLng);
		String currentAddress = addressConvertor.getAddress();
		text_location.setText(currentAddress);
		locationGeo = new ParseGeoPoint(locLat, locLng);
		locationL0 = currentAddress;
		locationL1 = addressConvertor.generalizeFirstLevel();
		locationL2 = addressConvertor.generalizeSecondLevel();
	}
}




////////////////////  EXTRA UNUSABLE CODES  ///////////////////////


//private String getCurrentTime(String dateFormat) {
//
/////// FIXed Timezone doesn't work properly! displayed right, but saved one hour wrong.
//
////DateFormat dateFormat1 = android.text.format.DateFormat.getBestDateTimePattern(, "MMMM")(getActivity().getApplicationContext());
//SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.GERMANY);
//TimeZone timeZone = TimeZone.getTimeZone("CET"); 
//df.setTimeZone(timeZone);
//Calendar cal = Calendar.getInstance();
//Date now = cal.getTime(); // set the current datetime in a Date-object
//String currentTime = df.format(now); // contains yyyy-MM-dd (e.g. 2012-03-15 for March 15, 2012)
//return currentTime;
//}

//text_current_time.setText(getCurrentTime("MMM dd, yyyy, HH:mm"));  // old one: "yyyy/MM/dd HH:mm"

///// !!! Timezone Problem !!! /////
//	text_current_time = (EditText) getActivity().findViewById(R.id.txt_time);
//	String time0Str = text_current_time.getText().toString();
//	SimpleDateFormat df0 = new SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.ENGLISH);
//	TimeZone timeZone = TimeZone.getTimeZone("CET"); 
//	df0.setTimeZone(timeZone);
//	Date time0 = new Date();
//	try {
//		time0 = df0.parse(time0Str);
//	} catch (java.text.ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	postObj.put("time0", time0);
//	
//	SimpleDateFormat df1 = new SimpleDateFormat("MMM dd, yyyy");
//	df1.setTimeZone(timeZone);
//	Date time1 = new Date();
//	try {
//		time1 = df1.parse(time0Str);
//	} catch (java.text.ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//
//	Calendar cal = Calendar.getInstance();
////	Date dateNow = null;
////	try {
////		dateNow = new SimpleDateFormat("MMM dd, yyyy").parse(time0Str);
////	} catch (java.text.ParseException e1) {
////		// TODO Auto-generated catch block
////		e1.printStackTrace();
////	}
//	//cal.setTime(time0); 
//	Log.d("TimeDebug", "" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + 
//			cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.DAY_OF_WEEK));
//	
//	DateFormat dfHour = new SimpleDateFormat("HH");
//	dfHour.setTimeZone(timeZone);
//	Date hour = new Date();
//	try {
//		hour = dfHour.parse(time0Str);
//	} catch (java.text.ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	Log.d("TimeDebug", hour.toString());
//	
//	String string = "January 2, 2010";
//	DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
//	try {
//		Date date = format.parse(string);
//		Log.d("TimeDebug", date.toString());
//	} catch (java.text.ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
