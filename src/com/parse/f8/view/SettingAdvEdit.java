package com.parse.f8.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.internal.ct;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.f8.R;
import com.parse.f8.R.layout;

import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class SettingAdvEdit extends Fragment {

	public static final String PARSE_ADV_PRIVACY_CLASS = "RestrictedList";
	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	public static final String ADV_SETTING_PREFS = "AdvSettingPrefs";
	ArrayList<RestrictedItem> advItemsList = new ArrayList<RestrictedItem>();
	ListView advItemsListView;
	AdvItemsListAdapter customAdapter;
	ProgressBar advItemsLoading;
	String userId;
	
	public SettingAdvEdit() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View advEditView = inflater.inflate(R.layout.setting_advedit, container, false);
		advItemsListView = (ListView) advEditView.findViewById(R.id.list_advEditList);
		advItemsLoading = (ProgressBar) advEditView.findViewById(R.id.progressBar_advEdit);
		
		if (fetchUserId() != null) {
			userId = fetchUserId();
			fetchRestrictedList();
			
		} else {
			Log.e("UserError", "The user does not Exist");
			Toast.makeText(getActivity().getApplicationContext(), "Error: The user does not exist! Please sign in again.", Toast.LENGTH_SHORT).show();
		}
		
		
		
		return advEditView;
	}

	private void fetchRestrictedList() {
		
		advItemsLoading.setVisibility(View.VISIBLE);
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_ADV_PRIVACY_CLASS);
		query.whereEqualTo("userId", userId);
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> advList, ParseException e) {
				
				if (e == null) {
											
					if (advList == null || advList.size()==0) {
						
						AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
						alertDialog.setTitle("Information");
						alertDialog.setMessage(getResources().getString(R.string.noAdvItemInfo));
						alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
						    new DialogInterface.OnClickListener() {
						        public void onClick(DialogInterface dialog, int which) {
						            dialog.dismiss();
						        }
						    });
						alertDialog.show();
					
					} else {
						
						for (ParseObject advItemObj : advList) {
							
							// Check items in Restricted List one by one
							int dayOfWeek = 0;
							String exactDate = "N/A";
							String dayPart   = "N/A";
							String[] timeSlot = {"N/A", "", ""};
							String locAddr   = "N/A";
							LatLng locLatLng = new LatLng(0, 0);
//							String coUser    = "N/A";
							List<String> coUserIdList = Arrays.asList("N/A");
//							String viUser    = "N/A";
							List<String> viUserIdList = Arrays.asList("N/A");
							
							String objId = advItemObj.getObjectId();
							
							if (advItemObj.getBoolean("timeFlag")) {
								
								dayOfWeek = advItemObj.getInt("dayOfWeek");
//								dayOfWeek = getDayofWeek(advItemObj.getInt("dayOfWeek"));
								if (dayOfWeek == 10) {
									exactDate = advItemObj.getString("exactDate");
								}
								dayPart = advItemObj.getString("timeDayPart");
								if (dayPart.equals("slot")) {
									timeSlot[0] = "A";
									timeSlot[1] = advItemObj.getString("timeStart2");
									timeSlot[2] = advItemObj.getString("timeEnd2");
//									dayPart = advItemObj.getString("timeStart2") + "-" + advItemObj.getString("timeEnd2");
								}
							} 
							
							if (advItemObj.getBoolean("locationFlag")) {
								
								locAddr = advItemObj.getString("locationAddr");
								ParseGeoPoint geoPoint = advItemObj.getParseGeoPoint("locationGeo");
								double lat = geoPoint.getLatitude();
								double lng = geoPoint.getLongitude();
								locLatLng = new LatLng(lat, lng);
							}
							
							if (advItemObj.getBoolean("coUserFlag")) {
				        		coUserIdList = advItemObj.getList("coUserIds");
							}
							
							if (advItemObj.getBoolean("viUserFlag")) {
								viUserIdList = advItemObj.getList("viUserIds");
							}
							
							String privacyProfile = advItemObj.getString("profile");
							int privacyId = advItemObj.getInt("identityLvl");
							int privacyTime = advItemObj.getInt("timeLvl");
							int privacyLoc = advItemObj.getInt("locationLvl");
							
							advItemsList.add(new RestrictedItem(objId, dayOfWeek, exactDate, dayPart, timeSlot, locAddr, locLatLng, coUserIdList, viUserIdList, privacyProfile, privacyId, privacyTime, privacyLoc));
						}
						
						customAdapter = new AdvItemsListAdapter(getActivity(), advItemsList);
						advItemsListView.setAdapter(customAdapter);
						advItemsLoading.setVisibility(View.GONE);
						advItemsListView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {
								
								saveDataToPrefs(customAdapter.getItem(position));
								Fragment newFragment = new SettingAdvEntry();
								Bundle bundle = new Bundle();
								bundle.putString("key", "edit");
								newFragment.setArguments(bundle); 
								FragmentTransaction transaction = getFragmentManager().beginTransaction();
							    transaction.replace(R.id.fragment_advedit, newFragment);
							    transaction.addToBackStack(null);
							    transaction.setTransition(4099);	
							    transaction.commit();
							}
						});
					}
				} else {
					Log.d("Parse_Error", "Error: Data not fetched " + e.getMessage());
				} 
			}
		});
		
	}
	
	private void saveDataToPrefs(RestrictedItem item) {
		
		SharedPreferences advSettingPref = this.getActivity().getSharedPreferences(ADV_SETTING_PREFS, 0);
		SharedPreferences.Editor editor = advSettingPref.edit();
		editor.clear();
		
		// Save Object ID
		editor.putString("objId", item.objId);		
		
		// Save Time Info
		if (item.dayOfWeek == 0 && item.dayPart.contains("N/A")) {
			editor.putBoolean("timeFlag", false);
		} else {
			editor.putBoolean("timeFlag", true);
			editor.putInt("dayOfWeek", item.dayOfWeek);
			editor.putString("exactDate", item.exactDate);
			editor.putString("timeDayPart", item.dayPart);
			editor.putString("timeStart", item.timeSlot[1]);
			editor.putString("timeEnd", item.timeSlot[2]);
		}

		// Save Location Info
		if (item.locInfo.contains("N/A")) {
			editor.putBoolean("locationFlag", false);
		} else {
			editor.putBoolean("locationFlag", true);
			editor.putString("locationAddr", item.locInfo);
			editor.putString("latitude", Double.toString(item.locLatLng.latitude));
			editor.putString("longitude", Double.toString(item.locLatLng.longitude));
		}

		// Save Co-User Info
		if (item.coUserIdList.get(0).contains("N/A")) {
			editor.putBoolean("coUserFlag", false);
		} else {
			editor.putBoolean("coUserFlag", true);
		    editor.putString("coUserIds", setUserInfoString(item.coUserIdList));
		}

		// Save Vi-User Info
		if (item.viUserIdList.get(0).contains("N/A")) {
			editor.putBoolean("viUserFlag", false);
		} else {
			editor.putBoolean("viUserFlag", true);
		    editor.putString("viUserIds", setUserInfoString(item.viUserIdList));
		}
		
		// Save Privacy Profile
		editor.putString("profile", item.privacyProfile);
		editor.putInt("identityLvl", item.privacyId);
		editor.putInt("timeLvl", item.privacyTime);
		editor.putInt("locationLvl", item.privacyLoc);
		
		editor.commit();
	}
	
	private String setUserInfoString(List<String> userList) {
		String userString = null;
	    for (String item : userList) {
	    	if (userString == null) {
	    		userString = item;
	    	} else {
	    		userString = userString + " , " + item;
	    	}
	    }
	    return userString;
	}
	
	private String fetchUserId() {
		
		SharedPreferences userInfoPref = this.getActivity().getSharedPreferences(USER_INFO_PREFS, 0);
		String userId = userInfoPref.getString("fbId", null);
		return userId;
	}
	
	@Override
	public void onResume() {
		fetchRestrictedList();
		super.onResume();
	}
}

class AdvItemsListAdapter extends BaseAdapter {

	Context context;
	ArrayList<RestrictedItem> advItemsList;
	
	public AdvItemsListAdapter(Context ctx, ArrayList<RestrictedItem> list) {
		
		context = ctx;
		advItemsList = list;
	}
	
	@Override
	public int getCount() {
		
		return advItemsList.size();
	}

	@Override
	public RestrictedItem getItem(int position) {
		
		return advItemsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView=inflater.inflate(R.layout.adv_edit_item, parent, false);
		TextView timeText = (TextView) itemView.findViewById(R.id.txt_advedit_time);
		TextView locText = (TextView) itemView.findViewById(R.id.txt_advedit_location);
		TextView coUserText = (TextView) itemView.findViewById(R.id.txt_advedit_couser);
		TextView viUserText = (TextView) itemView.findViewById(R.id.txt_advedit_viuser);
		TextView profileText = (TextView) itemView.findViewById(R.id.txt_advedit_privacyprofile);
		TextView timeLvlText = (TextView) itemView.findViewById(R.id.txt_advedit_privacytime);
		TextView locLvlText = (TextView) itemView.findViewById(R.id.txt_advedit_privacyloc);
		TextView idLvlText = (TextView) itemView.findViewById(R.id.txt_advedit_privacyid);
		
		RestrictedItem itemObj = advItemsList.get(position);
		
		String dayPart = itemObj.dayPart;
		String dayOfWeek = getDayofWeek(itemObj.dayOfWeek);
		if (itemObj.dayOfWeek == 10) {
			dayOfWeek = itemObj.exactDate;
		}
		if (dayPart.equals("slot")) {
			dayPart = itemObj.timeSlot[1] + "-" + itemObj.timeSlot[2];
		}
		if (dayPart.equals("N/A")) {
			timeText.setText(dayOfWeek);
			timeText.setTextColor(customTextColor(dayOfWeek));
		} else {
			timeText.setText(dayOfWeek + "\n" + dayPart);
		}
		
		
		locText.setText(itemObj.locInfo);
		locText.setTextColor(customTextColor(itemObj.locInfo));
		
		String coUserIdList = getUserListString(itemObj.coUserIdList);
		coUserText.setText(coUserIdList);
		coUserText.setTextColor(customTextColor(coUserIdList));
		
		String viUserIdList = getUserListString(itemObj.viUserIdList);
		viUserText.setText(viUserIdList);
		viUserText.setTextColor(customTextColor(viUserIdList));
		
		profileText.setText("<" + itemObj.privacyProfile + ">");
		profileText.setTextColor(customTextColor(itemObj.privacyProfile));
		
		String privacyTime = privacyLvlToString(itemObj.privacyTime);
		timeLvlText.setText(privacyTime);
		timeLvlText.setTextColor(customTextColor(privacyTime));
		
		String privacyLoc = privacyLvlToString(itemObj.privacyLoc);
		locLvlText.setText(privacyLoc);
		locLvlText.setTextColor(customTextColor(privacyLoc));
		
		String privacyId = privacyLvlToString(itemObj.privacyId);
		idLvlText.setText(privacyId);
		idLvlText.setTextColor(customTextColor(privacyId));
		
		return itemView;
	}
	
	private String getUserListString(List<String> userList) {
		
		String userString = "";
		if (userList != null && userList.size() != 0) {
			
			for (String user : userList) {
				userString = userString + user + ", ";
			}
			userString = userString.substring(0, userString.length()-2);
		}
		return userString;
	}
	
	private int customTextColor(String text) {
		
		int textColor = 0;
		if (text.equals("normal")) {
			textColor = Color.GREEN;
		}
		else if (text.equals("fair") || text.equals("low")) {
			textColor = Color.YELLOW;
		}
		else if (text.equals("strict") || text.equals("med")) {
			textColor = context.getResources().getColor(R.color.Orange);
		}
		else if (text.equals("full") || text.equals("high")) {
			textColor = Color.RED;
		}
		else if (text.equals("custom")) {
			textColor = Color.BLUE;
		}
		else if (text.equals("N/A")) {
			textColor = Color.GRAY;
		}
		else {
			textColor = Color.BLACK;
		}
		return textColor;
	}
	

	private String getDayofWeek(int dayOfWeek) {
		
		String dayStr = "N/A";
		switch (dayOfWeek) {
		case 1:
			dayStr = "Sundays";
			break;
		case 2:
			dayStr = "Mondays";
			break;
		case 3:
			dayStr = "Tuesdays";
			break;
		case 4:
			dayStr = "Wednesdays";
			break;
		case 5:
			dayStr = "Thursdays";
			break;
		case 6:
			dayStr = "Fridays";
			break;
		case 7:
			dayStr = "Saturdays";
			break;
		case 8:
			dayStr = "Weekends";
			break;
		case 9:
			dayStr = "Everyday";
			break;
		case 10:
			dayStr = "date";
			break;
		default:
			break;
		}
		return dayStr;
	}
	
	private String privacyLvlToString(int level) {
		
		if (level == 2) {
			return "high";
		}
		else if (level == 1) {
			return "med";
		}
		else {
			return "low";
		}
	}
}

class RestrictedItem {
	
	String objId;
	int dayOfWeek;
	String exactDate;
	String dayPart;
	String[] timeSlot; 
	String locInfo;
	LatLng locLatLng;
	List<String> coUserIdList;
//	String coUserInfo;
	List<String> viUserIdList;
//	String viUserInfo;
	String privacyProfile;
	int privacyId;
	int privacyTime;
	int privacyLoc;	
		
	RestrictedItem(String objId, int dayOfWeek, String exactDate, String dayPart, String[] timeSlot, 
			String locInfo, LatLng locLatLng, List<String> coUserIdList, List<String> viUserIdList,	
			String privacyProfile, int privacyId, int privacyTime, int privacyLoc) {
	
		this.objId=objId;
		this.dayOfWeek=dayOfWeek;
		this.exactDate=exactDate;
		this.dayPart=dayPart;
		this.timeSlot=timeSlot;
		this.locInfo=locInfo;
		this.locLatLng=locLatLng;
		this.coUserIdList=coUserIdList;
//		this.coUserInfo=coUserInfo;
		this.viUserIdList=viUserIdList;
//		this.viUserInfo=viUserInfo;
		this.privacyProfile=privacyProfile;
		this.privacyId=privacyId;
		this.privacyTime=privacyTime;
		this.privacyLoc=privacyLoc;
	}
}