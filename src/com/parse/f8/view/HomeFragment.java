package com.parse.f8.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.f8.R;
import com.parse.f8.R.id;
import com.parse.f8.R.layout;
import com.parse.f8.R.menu;


import android.support.v4.app.Fragment;
import android.animation.TimeAnimator.TimeListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
	
	public static final String PARSE_ADV_PRIVACY_CLASS = "RestrictedList";
	public static final String PARSE_SIMPLE_PRIVACY_CLASS = "PrivacyProfile";
	public static final String PARSE_POST_CLASS = "Post";
	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	ListView newsFeedListView;
	ArrayList<SingleItem> newsFeedList = new ArrayList<SingleItem>();
	JSONObject privacyPrefsObj = new JSONObject();
	ArrayList<String> privacyIdsHidden = new ArrayList<String>();
	ArrayList<String> privacyIdsGeneralized = new ArrayList<String>();
	ArrayList<String> finalFriends = new ArrayList<String>();
	Boolean anonymity = false;
	String ownerId;
	int photoId;
	Boolean privacyApply;
	Boolean noPrefsFriendsId;
	ProgressBar newsFeedLoading;
	
	public HomeFragment(){
		// Required empty public constructor
	}
	
	// FIXMED homeView gets inflated earlier than loading data from Parse and does not show them!
	// FIXME Implement Scenarios and test functionality!
	
	@Override
	public View onCreateView(LayoutInflater inflator, ViewGroup container,
			Bundle savedInstanceState){
		// Inflate the layout for this fragment
		View homeView = inflator.inflate(R.layout.fragment_home, container, false);
		newsFeedLoading = (ProgressBar) homeView.findViewById(R.id.progressBar_home);
		
//		ownerId = fetchUserInfo("fbId");
		ownerId = fetchUserInfo("firstName"); // FIXME fbId should be replaced!
		
		fetchNewsFeedList();
		
		newsFeedListView = (ListView) homeView.findViewById(R.id.list_newsfeed);
		
		return homeView;
		
	}
	
	public void fetchNewsFeedList() {
		
//		String status = null;
//		String friendTag = null;
		newsFeedList.clear();
		newsFeedLoading.setVisibility(View.VISIBLE);
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_POST_CLASS);
		query.orderByDescending("createdAt");
		query.setLimit(10);
		
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> postList, ParseException e) {
				if (e == null) {
					for (ParseObject post : postList) {
						
						// JSON PrivacyPrefsObj initialization
						try {
							privacyPrefsObj.put("timeLvl", 0);
							privacyPrefsObj.put("locationLvl", 0);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						finalFriends.clear();
						privacyIdsGeneralized.clear();
						privacyIdsHidden.clear();
						anonymity = false;
						privacyApply = false;
						noPrefsFriendsId = false;
						
						String username = post.getString("owner");
						String status = post.getString("text");
						String friendTag = "";
						Date timeTag = post.getDate("time0");
						String timeStr0Tag = post.getString("timeL0");
						String locTag = post.getString("locL0");
//						String userId = post.getString("userId");
						String userId = post.getString("owner"); // FIXME userId should be replaced!
						List<String> friendIdList = post.getList("friends");
						ParseGeoPoint locGeoTag = (ParseGeoPoint) post.get("locGeo");
						
//						String genderStr = post.getString("gender");
						Boolean genderMale = post.getBoolean("genderMale");
						if (!genderMale) {
							photoId = getResources().getIdentifier("female_avatar" , "drawable", getActivity().getPackageName());
						} else {
							photoId = getResources().getIdentifier("male_avatar" , "drawable", getActivity().getPackageName());
						}
						
						if (friendIdList != null) {
							for (String friendId : friendIdList) {
//								Log.d("MXfriends", friendId);
								checkPrivacyPreferences(friendId, post);
								checkRestrictedList(friendId, userId, friendIdList, timeTag, locGeoTag, post);
							}
							applyPrivacyPrefs(post);
						} else {	
							newsFeedList.add(new SingleItem(photoId, username, status, friendTag, timeStr0Tag, locTag));
						}
					}
					newsFeedListView.setAdapter(new NewsFeedListAdapter(HomeFragment.this.getActivity(),newsFeedList));
					newsFeedLoading.setVisibility(View.GONE);
				}
				else {
					Toast.makeText(getActivity().getApplicationContext(),	R.string.show_posts_error, Toast.LENGTH_LONG).show();
					Log.d("Parse_Error", "Error: Data not fetched " + e.getMessage());
				} 
			}
		});
		
		//return null;
		
	}
	
	private void checkPrivacyPreferences(final String userId, final ParseObject post) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_SIMPLE_PRIVACY_CLASS);
		query.whereEqualTo("user", userId); // FIXME "user" shoud be replaced by "userId"!
		List<ParseObject> userObj=null;
		try {
			userObj = query.find();
		} catch (ParseException e1) {
			Log.d("ParseError", "Error: " + e1.getMessage());
			e1.printStackTrace();
		}
//		query.findInBackground(new FindCallback<ParseObject>() {
//		// CHECK It might be faster, if parse data is loaded in MainActivity and stored in a shared preferences, then loaded to widgets here	
//			@Override
//			public void done(List<ParseObject> userObj, ParseException e) {
//
//		        if (e == null) {
		        	
		        	if (userObj == null || userObj.size()==0) {
//		        		Log.d("ParseQueryError", "There is no user object with user ID " + userId + 
//		        				" is defined in <" + PARSE_SIMPLE_PRIVACY_CLASS + "> Parse Class");
//		        		if (noPrefsFriendsId) {
//		        			finalFriends.add(userId);
//		        		} else {
	        			noPrefsFriendsId = true;
//		        		}

		        	} else {
		        		
		        		ParseObject user = userObj.get(0);
		        		setPrivacyPrefs(user, userId);
//		        		if (privacyApply) {
//		        			applyPrivacyPrefs(post);
//		        		} else {
//		        			privacyApply = true;
//		        		}
		        	}
		        	
//		        } else {
//		            Log.d("ParseError", "Error: " + e.getMessage());
//		        }
		        
//			}
//		});
	}
	
	private void checkRestrictedList(final String userId, final String postUserId, final List<String> friendIdList,
											final Date timeTag, final ParseGeoPoint locGeoTag, final ParseObject post) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_ADV_PRIVACY_CLASS);
		query.whereEqualTo("user", userId); // FIXME "user" shoud be replaced by "userId"!
		List<ParseObject> userObj=null;
		try {
			userObj = query.find();
		} catch (ParseException e1) {
			Log.d("ParseError", "Error: " + e1.getMessage());
			e1.printStackTrace();
		}
//		query.findInBackground(new FindCallback<ParseObject>() {
//		// CHECK It might be faster, if parse data is loaded in MainActivity and stored in a shared preferences, then loaded to widgets here	
//			@Override
//			public void done(List<ParseObject> userObj, ParseException e) {
//
//		        if (e == null) {
		        	
		        	if (userObj == null || userObj.size()==0) {
//		        		Log.d("ParseQueryError", "There is no user object with user ID " + userId + 
//		        				" is defined in <" + PARSE_ADV_PRIVACY_CLASS + "> Parse Class");
		        		if (noPrefsFriendsId) {
		        			finalFriends.add(userId);
//		        		} else {
//		        			noPrefsFriendsId = true;
		        		}

		        	} else {
		        		
		        		for (int i=0 ; i<userObj.size() ; i++) {
		        			
		        			ParseObject user = userObj.get(i);
		        			
			        		Boolean coUserFlag = false;
			        		Boolean viUserFlag = false;
			        		Boolean timeFlag = false;
			        		Boolean locationFlag = false;
			        		Boolean Restrictionflag = false;
			        		
			        		List<String> coUserIdList = user.getList("coUserIds");
			        		List<String> viUserIdList = user.getList("viUserIds");
			        		String timeStart = user.getString("timeStart2");
			        		String timeEnd = user.getString("timeEnd2");
			        		int dayOfWeek = user.getInt("dayOfWeek");
//			        		String locAddr = user.getString("locationAddr"); 
			        		ParseGeoPoint userGeoLocation = (ParseGeoPoint) user.get("locationGeo");
			        		
			        		Boolean parseTimeFlag = user.getBoolean("timeFlag");
			        		Boolean parseLocationFlag = user.getBoolean("locationFlag");
			        		Boolean parseCoUserFlag = user.getBoolean("coUserFlag");
			        		Boolean parseViUserFlag = user.getBoolean("viUserFlag");
			        		
			        		// CHECK consider if the person himself is in his restricted coUserId list!
			        		
			        		// check CO-USER hit
			        		if (parseCoUserFlag) {
				        		if (coUserIdList != null) {
					        		for (String coUserId : coUserIdList) {
					        			for (String friendId : friendIdList) {
					        				if (coUserId.equalsIgnoreCase(friendId)) {
					        					coUserFlag = true;
					        				}
					        			}
					        			if (coUserId == postUserId) {
					        				coUserFlag = true;
					        			}
					        		}
				        		}
			        		} else {
			        			coUserFlag = true;
			        		}
			        		
			        		// check VI-USER hit
			        		if (coUserFlag) {
			        			if (parseViUserFlag) {
					        		if (viUserIdList != null) {
						        		for (String viUserId : viUserIdList) {
						        			if (viUserId.equalsIgnoreCase(ownerId)) {
						        				viUserFlag = true;
						        			}
						        		}
					        		}
				        		} else {
				        			viUserFlag = true;
				        		}
			        		}
	
			        		// check TIME data hits
			        		if (viUserFlag) {
			        			if (parseTimeFlag) {
				        			Calendar timeCal = Calendar.getInstance();
				        			timeCal.setTime(timeTag);
		
				        			Boolean dayFlag = false;
				        			int dayTag = timeCal.get(Calendar.DAY_OF_WEEK);
				        			// FIXME Specific time is not considered!!!
				        			if(dayOfWeek == 9) {
				        				dayFlag = true;
				        			}
				        			else if (dayOfWeek == 8) {
				        				if (dayTag == 1 || dayTag == 7) {
				        					dayFlag = true;
				        				}
				        			}
				        			else {
				        				if (dayOfWeek == dayTag) {
				        					dayFlag = true;
				        				}
				        			}
				        			
				        			Date time1 = getDate(timeCal, timeStart);
				        			Date time2 = getDate(timeCal, timeEnd);
				        			if (dayFlag) {
					        			if (timeTag.after(time1) && timeTag.before(time2)) {
					        				
					        				timeFlag = true;
					        			}
				        			}
			        			} else {
			        				timeFlag = true;
			        		}
	
			        			// check LOCATION data hits
			        			if (timeFlag) {
			        				
			        				if (parseLocationFlag) {
				        				if (userGeoLocation.distanceInKilometersTo(locGeoTag) < 0.5) {
				        					locationFlag = true;
				        				}
			        				} else {
			        					locationFlag = true;
			        				}
			        			}
			        			
			        			if (locationFlag) {
			        				Restrictionflag = true;
			        			}
			        			
			        			
			        			if (Restrictionflag) {
			        				
			        				setPrivacyPrefs(user, userId);
			        			}
	
			        			
			        			// TASK if both time and location return true, RestrictionTag is true and privacy prefs get checked!
			        		}
			        	}
		        		
//		        		if (privacyApply) {
//		        			applyPrivacyPrefs(post);
//		        		} else {
//		        			privacyApply = true;
//		        		}
		        	}
		        	
//		        } else {
//		            Log.d("ParseError", "Error: " + e.getMessage());
//		        }
//		        
//			}
//		});
	}
	
	private void setPrivacyPrefs(ParseObject user, String userId) {
		
		int identityLvlParse = user.getInt("identityLvl");
		if (identityLvlParse == 0) {
			if (!privacyIdsGeneralized.contains(userId)) {
				if (!privacyIdsHidden.contains(userId)) {
					finalFriends.add(userId);
				}
			}
			
		}
		else if (identityLvlParse == 1) {
			anonymity = true;
			privacyIdsGeneralized.add(userId);
			if (finalFriends.contains(userId)) {
				finalFriends.remove(userId);
			}
		}
		else if (identityLvlParse == 2) {
			privacyIdsHidden.add(userId);
			if (finalFriends.contains(userId)) {
				finalFriends.remove(userId);
			}
			if (privacyIdsGeneralized.contains(userId)) {
				anonymity = false;
				privacyIdsGeneralized.remove(userId);
			}
		}
		
		
		int timeLvlParse = user.getInt("timeLvl");
		int locationLvlParse = user.getInt("locationLvl");
		int timeLvlJSON = 0;
		int locationLvlJSON = 0;
		try {
			timeLvlJSON = privacyPrefsObj.getInt("timeLvl");
			locationLvlJSON = privacyPrefsObj.getInt("locationLvl");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		
		if (timeLvlParse > timeLvlJSON) {
			
			try {
				privacyPrefsObj.put("timeLvl", timeLvlParse);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		if (locationLvlParse > locationLvlJSON) {
			
			try {
				privacyPrefsObj.put("locationLvl", locationLvlParse);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	private boolean userInBlackList(String userId) {
		
		boolean inBlackList = false;
		
		
		return inBlackList;
	}
	
	private void applyPrivacyPrefs(ParseObject post) {
		
		String username = post.getString("owner");
		String status = post.getString("text");
		
		String timeTag = null;
		int timeLvl = 0;
		int locLvl = 0;
		try {
			timeLvl = privacyPrefsObj.getInt("timeLvl");
			locLvl = privacyPrefsObj.getInt("locationLvl");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch (timeLvl) {
		case 0:
			timeTag = post.getString("timeL0");
			break;
		case 1:
			timeTag = post.getString("timeL1");
			break;
		case 2:
			timeTag = post.getString("timeL2");
			break;
		default:
			timeTag = post.getString("timeL0");
			break;
		}

		String locTag = null;
		switch (locLvl) {
		case 0:
			locTag = post.getString("locL0");
			break;
		case 1:
			locTag = post.getString("locL1");
			break;
		case 2:
			locTag = post.getString("locL2");
			break;
		default:
			locTag = post.getString("locL0");
			break;
		}
		
		
		String friendsTag = null;
		if (finalFriends != null && finalFriends.size() != 0) {
			for (int i=0 ; i<finalFriends.size() ; i++) {
				if (friendsTag == null) {
					friendsTag = finalFriends.get(i);
				} else {
					if (anonymity) {
						friendsTag = friendsTag + ", " + finalFriends.get(i);
					} else {
						if (i == finalFriends.size()-1) {
							friendsTag = friendsTag + " and " + finalFriends.get(i);
						} else {
							friendsTag = friendsTag + ", " + finalFriends.get(i);
						}
					}

				}
			}
		}
		
		if (anonymity) {
			if (finalFriends.size() == 0) {
				friendsTag = "friends";
			} else {
				friendsTag = friendsTag + " and friends";
			}
		}
		
		
//		else {
//			friendsTag = "friends";
//		}
		
//		Boolean foundMatch = false;
//		List<String> friendIdList = post.getList("friends");
//		for (String friendId : friendIdList) {
//			if (privacyIdsGeneralized != null) {
//				for (String restrictedId : privacyIdsGeneralized) {
//					if (friendId != restrictedId) {
//						foundMatch = true;
//					}
//				}
//			}
//			if (foundMatch) {
//				anonymousId = true;
//			} else {
//				
//			}
//		}
		
		newsFeedList.add(new SingleItem(photoId, username, status, friendsTag, timeTag, locTag));
		
	}
	
	private Date getDate(Calendar cal, String timeStr) {
		 
		List<String> timeHM = Arrays.asList(timeStr.split(":"));
		int timeH = Integer.parseInt(timeHM.get(0));
		int timeM = Integer.parseInt(timeHM.get(1));
	    cal.set(Calendar.HOUR_OF_DAY, timeH);  
	    cal.set(Calendar.MINUTE, timeM);  
	    return cal.getTime(); 
	}
	
	private String fetchUserInfo(String type) {
		
		SharedPreferences userInfoPref = this.getActivity().getSharedPreferences(USER_INFO_PREFS, 0);
		String userInfo = userInfoPref.getString(type, null);
		
		return userInfo;
	}
	
}

class NewsFeedListAdapter extends BaseAdapter {

	
	Context context;
	ArrayList<SingleItem> newsFeedList1;
	
	NewsFeedListAdapter(Context ctx, ArrayList<SingleItem> feedlist) {
	
		context=ctx;
		newsFeedList1 =feedlist;
//		Resources res=ctx.getResources();
//		String[] titles=res.getStringArray(R.array.drawer_schedule_content);
//		String[] descs=res.getStringArray(R.array.drawer_schedule_times);
//		int[] imgs={R.drawable.grow,R.drawable.lunch,R.drawable.registration,R.drawable.monetize,R.drawable.hackerway,R.drawable.build};
		
//		for(int i=0;i<6;i++) {
//			
//			newsFeedList.add(new SingleItem(titles[i], descs[i], imgs[i]));
//		}
		
		
	}	
	
	@Override
	public int getCount() {
		
		return newsFeedList1.size();
	}

	@Override
	public Object getItem(int i) {
		
		return newsFeedList1.get(i);
	}

	@Override
	public long getItemId(int i) {
		
				return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		// TASK Implement ViewHolder class for better performance
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView=inflater.inflate(R.layout.news_feed_item, viewGroup, false);
		ImageView img = (ImageView) itemView.findViewById(R.id.img_userphoto);
		TextView userName = (TextView) itemView.findViewById(R.id.txt_username_home);
		TextView status = (TextView) itemView.findViewById(R.id.txt_status_home);
		TextView friendTag = (TextView) itemView.findViewById(R.id.txt_with_home);
		TextView timeTag = (TextView) itemView.findViewById(R.id.txt_time_home);
		TextView locTag = (TextView) itemView.findViewById(R.id.txt_loc_home);
		
		SingleItem itemObj = newsFeedList1.get(i);
		
		img.setImageResource(itemObj.image);
		userName.setText(itemObj.username);
		status.setText(itemObj.status);
		friendTag.setText(itemObj.friends);
		timeTag.setText(itemObj.time);
		locTag.setText(itemObj.location);
		
		
		return itemView;
	}
	
		
}

class SingleItem {
	
	int image;
	String username;
	String status;
	String friends;
	String time;
	String location;
		
	SingleItem(int image, String username, String status, String friends, String time, String location) {
	
		this.image=image;
		this.username=username;
		this.status=status;
		this.friends=friends;
		this.time=time;
		this.location=location;
		
				
	}
}



//public class HomeFragment extends ActionBarActivity {
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.fragment_home);
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.home, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	/**
//	 * A placeholder fragment containing a simple view.
//	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_home, container,
//					false);
//			return rootView;
//		}
//	}
//}
