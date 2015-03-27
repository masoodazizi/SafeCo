package com.parse.f8.view;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.ct;
import com.parse.FindCallback;
import com.parse.ParseException;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	ArrayList<RestrictedItem> advItemsList = new ArrayList<RestrictedItem>();
	ListView advItemsListView;
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
							String dayOfWeek = "N/A";
							String dayPart   = "N/A";
							String locAddr   = "N/A";
							String coUser    = "N/A";
							String viUser    = "N/A";
							
							String objId = advItemObj.getObjectId();
							
							if (advItemObj.getBoolean("timeFlag")) {
								
								dayOfWeek = getDayofWeek(advItemObj.getInt("dayOfWeek"));
								if (dayOfWeek.equals("date")) {
									dayOfWeek = advItemObj.getString("exactDate");
								}
								dayPart = advItemObj.getString("timeDayPart");
								if (dayPart.equals("slot")) {
									dayPart = advItemObj.getString("timeStart2") + "-" + advItemObj.getString("timeEnd2");
								}
							} 
							
							if (advItemObj.getBoolean("locationFlag")) {
								
								locAddr = advItemObj.getString("locationAddr");
							}
							
							if (advItemObj.getBoolean("coUserFlag")) {
								
				        		List<String> coUserIdList = advItemObj.getList("coUserIds");
								if (coUserIdList != null && coUserIdList.size() != 0) {
									coUser = "";
									for (String user : coUserIdList) {
										coUser = coUser + user + ", ";
									}
									coUser = coUser.substring(0, coUser.length()-2);
								}
							}
							
							if (advItemObj.getBoolean("viUserFlag")) {
								
								List<String> viUserIdList = advItemObj.getList("viUserIds");
								if (viUserIdList != null && viUserIdList.size() != 0) {
									viUser = "";
									for (String user : viUserIdList) {
										viUser = viUser + user + ", ";
									}
									viUser = viUser.substring(0, viUser.length()-2);
								}
							}
							
							String privacyProfile = advItemObj.getString("profile");
							String privacyId = privacyLvlToString(advItemObj.getInt("identityLvl"));
							String privacyTime = privacyLvlToString(advItemObj.getInt("timeLvl"));
							String privacyLoc = privacyLvlToString(advItemObj.getInt("locationLvl"));
							
							advItemsList.add(new RestrictedItem(objId, dayOfWeek, dayPart, locAddr, coUser, viUser, privacyProfile, privacyId, privacyTime, privacyLoc));
						}
						
						advItemsListView.setAdapter(new AdvItemsListAdapter(getActivity(), advItemsList));
						advItemsLoading.setVisibility(View.GONE);
					}
				} else {
					Log.d("Parse_Error", "Error: Data not fetched " + e.getMessage());
				} 
			}
		});
		
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
	
	private String fetchUserId() {
		
		SharedPreferences userInfoPref = this.getActivity().getSharedPreferences(USER_INFO_PREFS, 0);
		String userId = userInfoPref.getString("fbId", null);
		return userId;
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
	public Object getItem(int position) {
		
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
		
		if (itemObj.timeInfo2.equals("N/A")) {
			timeText.setText(itemObj.timeInfo1);
			timeText.setTextColor(customTextColor(itemObj.timeInfo1));
		} else {
			timeText.setText(itemObj.timeInfo1 + "\n" + itemObj.timeInfo2);
		}
		
		
		locText.setText(itemObj.locInfo);
		locText.setTextColor(customTextColor(itemObj.locInfo));
		
		coUserText.setText(itemObj.coUserInfo);
		coUserText.setTextColor(customTextColor(itemObj.coUserInfo));
		
		viUserText.setText(itemObj.viUserInfo);
		viUserText.setTextColor(customTextColor(itemObj.viUserInfo));
		
		profileText.setText("<" + itemObj.privacyProfile + ">");
		profileText.setTextColor(customTextColor(itemObj.privacyProfile));
		
		timeLvlText.setText(itemObj.privacyTime);
		timeLvlText.setTextColor(customTextColor(itemObj.privacyTime));
		
		locLvlText.setText(itemObj.privacyLoc);
		locLvlText.setTextColor(customTextColor(itemObj.privacyLoc));
		
		idLvlText.setText(itemObj.privacyId);
		idLvlText.setTextColor(customTextColor(itemObj.privacyId));
		
		return itemView;
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
	
}

class RestrictedItem {
	
	String objId;
	String timeInfo1;
	String timeInfo2;
	String locInfo;
	String coUserInfo;
	String viUserInfo;
	String privacyProfile;
	String privacyId;
	String privacyTime;
	String privacyLoc;	
		
	RestrictedItem(String objId, String timeInfo1, String timeInfo2, String locInfo, String coUserInfo, String viUserInfo,	
			String privacyProfile, String privacyId, String privacyTime, String privacyLoc) {
	
		this.objId=objId;
		this.timeInfo1=timeInfo1;
		this.timeInfo2=timeInfo2;
		this.locInfo=locInfo;
		this.coUserInfo=coUserInfo;
		this.viUserInfo=viUserInfo;
		this.privacyProfile=privacyProfile;
		this.privacyId=privacyId;
		this.privacyTime=privacyTime;
		this.privacyLoc=privacyLoc;
	}
}