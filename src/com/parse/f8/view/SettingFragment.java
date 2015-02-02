package com.parse.f8.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.f8.R;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class SettingFragment extends Fragment {

	public SettingFragment() {
		// Required empty public constructor
	}
	
	public static ProfilePictureView profilePictureView;
	public static TextView userNameView;
	private TextView txt;
	public static final String MY_PREFS_NAME = "MyPrefsFile";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

	    View settingView = inflater.inflate(R.layout.fragment_setting, container, false);
//		profilePictureView = (ProfilePictureView) settingView.findViewById(R.id.profile_pic);
//		profilePictureView.setCropped(true);
//	    userNameView = (TextView) settingView.findViewById(R.id.profile_username);
//	
//	    txt = (TextView) settingView.findViewById(R.id.txtName);
//
//	    
//	    SharedPreferences settings = this.getActivity().getSharedPreferences(MY_PREFS_NAME, 0);
//	    String highScore = settings.getString("name", "No name defined");
//		txt.setText(highScore);
	    
//		String imgPath = settings.getString("imgUrl", null);
//		URL newurl = null;
//		try {
//			newurl = new URL(imgPath);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Bitmap mIcon_val = null;
//		try {
//			mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		ImageView profilePhoto = (ImageView) settingView.findViewById(R.id.profile_photo);
//		profilePhoto.setImageBitmap(mIcon_val);
//		
	    return settingView;
	}
	

	
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
}
