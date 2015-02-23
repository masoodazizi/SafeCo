package com.parse.f8.view;

import java.util.ArrayList;
import java.util.List;

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
	
	ListView newsFeedListView;
	ArrayList<SingleItem> newsFeedList = new ArrayList<SingleItem>();
	
	public HomeFragment(){
		// Required empty public constructor
	}
	
	// FIXME homeView gets inflated earlier than loading data from Parse and does not show them!
	
	@Override
	public View onCreateView(LayoutInflater inflator, ViewGroup container,
			Bundle savedInstanceState){
		// Inflate the layout for this fragment
		View homeView = inflator.inflate(R.layout.fragment_home, container, false);
		
		fetchNewsFeedList();
		
		newsFeedListView = (ListView) homeView.findViewById(R.id.list_newsfeed);
		
		
		
		return homeView;
		
	}
	
	public void fetchNewsFeedList() {
		
//		String status = null;
//		String friendTag = null;
		newsFeedList.clear();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
		query.orderByDescending("createdAt");
		query.setLimit(10);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> postList, ParseException e) {
				if (e == null) {
					for (ParseObject post : postList) {
						String username = post.getString("owner");
						String status = post.getString("text");
						String friendTag = post.getString("friend");
						String timeTag = post.getDate("time0").toString();
						String locTag = post.getString("loc0");
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
	
}

class NewsFeedListAdapter extends BaseAdapter {

	
	Context context;
	ArrayList<SingleItem> newsFeedList1;
	
	NewsFeedListAdapter(Context ctx, ArrayList<SingleItem> feedlist) {
	
		context=ctx;
		newsFeedList1 =feedlist;
		Resources res=ctx.getResources();
		String[] titles=res.getStringArray(R.array.drawer_schedule_content);
		String[] descs=res.getStringArray(R.array.drawer_schedule_times);
		int[] imgs={R.drawable.grow,R.drawable.lunch,R.drawable.registration,R.drawable.monetize,R.drawable.hackerway,R.drawable.build};
		
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
