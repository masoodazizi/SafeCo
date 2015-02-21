package com.parse.f8.view;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.f8.R;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class ProfileFragment extends Fragment {

	public static final String PROFILE_PIC_PREF = "profilePicPrefs";
	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	
	private EditText text_current_time;
	private EditText text_status;
	
	
	public ProfileFragment() {
		// Required empty public constructor
		
				
		// Test Section //
		
		
//		ParseObject newObj = new ParseObject("NewObj");
//		newObj.put("ProjName", "Thesis");
//		newObj.put("Author", "Masood Azizi");
//		newObj.put("Deadline", "May 2015");
////		newObj.saveInBackground();
//
//		
//		ParseQuery<ParseObject> query = ParseQuery.getQuery("NewObj");
//		query.whereEqualTo("ProjName", "Thesis");
//		query.findInBackground(new FindCallback<ParseObject>() {
//						
//			@Override
//			public void done(List<ParseObject> result, ParseException e) {
//				// TODO Auto-generated method stub
//				if (e == null)
//					Log.d("mylog", "Result is " + result.size());
//				else
//					Log.d("mylog", "Error! " + e.getMessage());
//			}
//		});
		
		
		/////////////////
		
	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
		
		// Fetch profile photo path and load it to ImageView
		setProfilePhoto(profileView);
		setProfileInfo(profileView);
		
		text_current_time = (EditText) profileView.findViewById(R.id.txt_time);
		text_current_time.setText(getCurrentTime("MMM dd, yyyy, HH:mm"));  // old one: "yyyy/MM/dd HH:mm"
		
		Button postButton = (Button) profileView.findViewById(R.id.btn_post);
		postButton.setOnClickListener(new  View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				onPostButtonClicked(v);
			}
		});
		
		return profileView;
	}
	
	
	private void onPostButtonClicked(View v) {

		
		text_status = (EditText) getActivity().findViewById(R.id.txt_status_text);
		String status = text_status.getText().toString();
		text_status.setText("");
		
		///// !!! Timezone Problem !!! /////
		text_current_time = (EditText) getActivity().findViewById(R.id.txt_time);
		String time0Str = text_current_time.getText().toString();
		SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy, HH:mm");
		TimeZone timeZone = TimeZone.getTimeZone("CET"); 
		df.setTimeZone(timeZone);
		Date time0 = new Date();
		try {
			time0 = df.parse(time0Str);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String name = fetchUserInfo("name");
		
		ParseObject postObj = new ParseObject("Post");
		postObj.put("owner", name);
		postObj.put("text", status);
		postObj.put("time0", time0);
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
		String userInfo = userInfoPref.getString(type, "None");
		
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
}
