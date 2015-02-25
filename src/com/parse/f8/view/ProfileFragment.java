package com.parse.f8.view;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
	
	private EditText text_current_time;
	private EditText text_status;
	private EditText text_location;
	private ImageView imageMapPin;
	private View datePicker;
	private ImageView timePicker;
	private String latitude = null;
	private String longitude = null;
	private int selectedHour;
	private int selectedMin;
	private int selectedYear;
	private int selectedMonth;
	private int selectedDay;
	
	public ProfileFragment() {
		
	}
	
	@Override
	public void onResume() {
		
		Log.d("DEBUG", "onResume of Profile fragment");
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
		
		// Fetch profile photo path and load it to ImageView
		setProfilePhoto(profileView);
		setProfileInfo(profileView);
		removeLocPrefsKeys();
		
		text_current_time = (EditText) profileView.findViewById(R.id.txt_time);
		text_current_time.setText(getCurrentTime("MMM dd, yyyy, HH:mm"));  // old one: "yyyy/MM/dd HH:mm"
		
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
					try {
						address = addressConvertor.getAddress();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					text_location = (EditText) profileView.findViewById(R.id.txt_location);
					text_location.setText(address);
				}
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
		
		// STATUS update
		text_status = (EditText) getActivity().findViewById(R.id.txt_status_text);
		String status = text_status.getText().toString();
		text_status.setText("");
		postObj.put("text", status);
		
		// FRIEND UPDATE
		EditText txtFriendList = (EditText) getActivity().findViewById(R.id.txt_with);
		String friendListString = txtFriendList.getText().toString();
		if (friendListString != null && friendListString != "") {
			
			List<String> friendsList = Arrays.asList(friendListString.split("\\s*,\\s*"));
			postObj.put("friends", friendsList);
			txtFriendList.setText("");
		}
			
		
		// TIME update
		///// !!! Timezone Problem !!! /////
		text_current_time = (EditText) getActivity().findViewById(R.id.txt_time);
		String time0Str = text_current_time.getText().toString();
		SimpleDateFormat df0 = new SimpleDateFormat("MMM dd, yyyy, HH:mm");
		TimeZone timeZone = TimeZone.getTimeZone("CET"); 
		df0.setTimeZone(timeZone);
		Date time0 = new Date();
		try {
			time0 = df0.parse(time0Str);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postObj.put("time0", time0);
		
		SimpleDateFormat df1 = new SimpleDateFormat("MMM dd, yyyy");
		df1.setTimeZone(timeZone);
		Date time1 = new Date();
		try {
			time1 = df1.parse(time0Str);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		Calendar cal = Calendar.getInstance();
//		Date dateNow = null;
//		try {
//			dateNow = new SimpleDateFormat("MMM dd, yyyy").parse(time0Str);
//		} catch (java.text.ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		//cal.setTime(time0);
		Log.d("TimeDebug", "" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + 
				cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.DAY_OF_WEEK));
		
		DateFormat dfHour = new SimpleDateFormat("HH");
		dfHour.setTimeZone(timeZone);
		Date hour = new Date();
		try {
			hour = dfHour.parse(time0Str);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("TimeDebug", hour.toString());
		
		
		postObj.saveInBackground();
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
	        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
	            
	        profile_photo.setImageBitmap(b);
	    } 
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }

	}
	
	private void setProfilePhoto(View profileView){
		
		ImageView profile_photo=(ImageView) profileView.findViewById(R.id.profile_photo);
		String imgPath = "";
		imgPath = fetchPathPref();
		loadImageFromStorage(imgPath, profile_photo);
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
	
	private String getCurrentTime(String dateFormat) {
		
		///// FIXME Timezone doesn't work properly! displayed right, but saved one hour wrong.
		
//		DateFormat dateFormat1 = android.text.format.DateFormat.getBestDateTimePattern(, "MMMM")(getActivity().getApplicationContext());
		SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.GERMANY);
		TimeZone timeZone = TimeZone.getTimeZone("CET"); 
		df.setTimeZone(timeZone);
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime(); // set the current datetime in a Date-object
		String currentTime = df.format(now); // contains yyyy-MM-dd (e.g. 2012-03-15 for March 15, 2012)
		return currentTime;
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
		
		final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dpd = new DatePickerDialog(getActivity(), 
				new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						
						selectedYear = year;
						selectedMonth = monthOfYear;
						selectedDay = dayOfMonth;	
					}
				}, mYear, mMonth, mDay);
		dpd.show();
	}
	
	private void onTimePickerClicked() {
		
		// Process to get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        
        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                            int minute) {
                    
                    	selectedHour = hourOfDay;
                    	selectedMin = minute;
                        	                    }
                }, mHour, mMinute, false);
        tpd.show();
	}
}
