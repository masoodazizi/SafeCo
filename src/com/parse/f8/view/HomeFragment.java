package com.parse.f8.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.f8.R;
import com.parse.f8.R.id;
import com.parse.f8.R.layout;
import com.parse.f8.R.menu;


import android.support.v4.app.Fragment;
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
	String ownerId;
	
	
	public HomeFragment(){
		// Required empty public constructor
	}
	
	// FIXMED homeView gets inflated earlier than loading data from Parse and does not show them!
	
	@Override
	public View onCreateView(LayoutInflater inflator, ViewGroup container,
			Bundle savedInstanceState){
		// Inflate the layout for this fragment
		View homeView = inflator.inflate(R.layout.fragment_home, container, false);
		ownerId = fetchUserInfo("fbId");
		
		fetchNewsFeedList();
		
		newsFeedListView = (ListView) homeView.findViewById(R.id.list_newsfeed);
		
		return homeView;
		
	}
	
	public void fetchNewsFeedList() {
		
//		String status = null;
//		String friendTag = null;
		newsFeedList.clear();
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
						
						String username = post.getString("owner");
						String status = post.getString("text");
						String friendTag = post.getString("friend");
						String timeTag = post.getDate("time0").toString();
						String locTag = post.getString("loc0");
						String userId = post.getString("userId");
						List<String> friendIdList = post.getList("friends");
						if (friendIdList != null) {
							
							for (String friendId : friendIdList) {
								Log.d("MXfriends", friendId);
								checkPrivacyPreferences(friendId);
								checkRestrictedList(friendId, userId, friendIdList, timeTag, locTag);
							}

						}


						newsFeedList.add(new SingleItem(0, username, status, friendTag, timeTag, locTag));
					}
					newsFeedListView.setAdapter(new NewsFeedListAdapter(HomeFragment.this.getActivity(),newsFeedList));
				}
				else {
					Toast.makeText(getActivity().getApplicationContext(),	R.string.show_posts_error, Toast.LENGTH_LONG).show();
					Log.d("Parse_Error", "Error: Data not fetched " + e.getMessage());
				} 
			}
		});
		
		//return null;
		
	}
	
	private void checkPrivacyPreferences(final String userId) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_SIMPLE_PRIVACY_CLASS);
		query.whereEqualTo("user", userId); // TASK After test "user" shoud be replaced by "userId"!
		query.findInBackground(new FindCallback<ParseObject>() {
		// CHECK It might be faster, if parse data is loaded in MainActivity and stored in a shared preferences, then loaded to widgets here	
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {

		        if (e == null) {
		        	
		        	if (userObj == null || userObj.size()==0) {
		        		Log.d("ParseQueryError", "There is no user object with user ID " + userId + 
		        				" is defined in <" + PARSE_SIMPLE_PRIVACY_CLASS + "> Parse Class");

		        	} else {
		        		
		        		ParseObject user = userObj.get(0);
		        		if (user.getInt("identityLvl") == 1) {
		        			privacyIdsGeneralized.add(userId);
		        		}
		        		else if (user.getInt("identityLvl") == 2) {
		        			privacyIdsHidden.add(userId);
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
		        	
		        } else {
		            Log.d("ParseError", "Error: " + e.getMessage());
		        }
		        
			}
		});
	}
	
	private void checkRestrictedList(final String userId, String postUserId, final List<String> friendIdList,
											String timeTag, String locTag) {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_ADV_PRIVACY_CLASS);
		query.whereEqualTo("user", userId); // TASK After test "user" shoud be replaced by "userId"!
		query.findInBackground(new FindCallback<ParseObject>() {
		// CHECK It might be faster, if parse data is loaded in MainActivity and stored in a shared preferences, then loaded to widgets here	
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {

		        if (e == null) {
		        	
		        	if (userObj == null || userObj.size()==0) {
		        		Log.d("ParseQueryError", "There is no user object with user ID " + userId + 
		        				" is defined in <" + PARSE_ADV_PRIVACY_CLASS + "> Parse Class");

		        	} else {
		        		
		        		Boolean userFlag = false;
		        		Boolean Restrictionflag = false;
		        		ParseObject user = userObj.get(0);
		        		List<String> coUserIdList = user.getList("coUserIds");
		        		List<String> viUserIdList = user.getList("viUserIds");
		        		String timeStart = user.getString("timeStart2");
		        		String timeEnd = user.getString("timeEnd2");
		        		String timePeriod = user.getString("timePeriod");
		        		String locAddr = user.getString("locationAddr"); 
		        		
		        		// CHECK consider if the person himself is in his restricted coUserId list!
		        		for (String coUserId : coUserIdList) {
		        			for (String friendId : friendIdList) {
		        				if (coUserId == friendId) {
		        					userFlag = true;
		        				}
		        			}
		        		}
		        		
		        		for (String viUserId : viUserIdList) {
		        			if (viUserId == ownerId) {
		        				userFlag = true;
		        			}
		        		}
		        		
		        		if (userFlag) {
		        			
		        			List<String> time1 = Arrays.asList(timeStart.split(":"));
		        			int time1H = Integer.parseInt(time1.get(0));
		        			int time1M = Integer.parseInt(time1.get(1));
		        			List<String> time2 = Arrays.asList(timeEnd.split(":"));
		        			int time2H = Integer.parseInt(time2.get(0));
		        			int time2M = Integer.parseInt(time2.get(1));
		        			// TASK create a function to parse timeTag and extract time to compare
		        			// TASK create a function to parse timeTag and extract day of week
		        			
		        			// TASK create a function to parse location and compare
		        			
		        			// TASK if both time and location return true, RestrictionTag is true and privacy prefs get checked!
		        		}
		        	}
		        	
		        } else {
		            Log.d("ParseError", "Error: " + e.getMessage());
		        }
		        
			}
		});
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
		
	SingleItem(int image, String username, String title, String descrption, String time, String location) {
	
		this.image=image;
		this.username=username;
		this.status=title;
		this.friends=descrption;
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
