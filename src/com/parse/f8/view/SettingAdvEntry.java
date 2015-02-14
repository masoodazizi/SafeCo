package com.parse.f8.view;

import com.parse.f8.R;
import com.parse.f8.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
// FIXME The "Add Entry" button is not displayed in the layout!

public class SettingAdvEntry extends Fragment {

	String[] advEntryListItems;
	
	public SettingAdvEntry() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View advEntryView = inflater.inflate(R.layout.setting_adventry, container, false);
		
		advEntryListItems = getResources().getStringArray(R.array.adventry_list_items);
		ListView advEntryListView = (ListView) advEntryView.findViewById(R.id.listAdvEntry);
		ListAdapter advEntryAdapter = new ArrayAdapter<String>
					(getActivity(), R.layout.entry_item, R.id.lbl_entryItemTitle ,advEntryListItems);
		advEntryListView.setAdapter(advEntryAdapter);
		
		advEntryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Fragment newFragment = null;
				Log.d("myDebug", "Selected Item:" + Integer.toString(position) );
				
				switch (position) {
				case 0:
					newFragment = new SettingAdvTime();
					break;
				case 1:
					newFragment = new SettingAdvLocation();
					break;
				case 2:
					newFragment = new SettingAdvCoUser();
					break;
				case 3:
					newFragment = new SettingAdvViUser();
					break;
				case 4:
					newFragment = new SettingFragment();
					final Bundle bundle = new Bundle();
					bundle.putString("key", "add");
					Log.i("BUNDLE", bundle.toString());
					newFragment.setArguments(bundle); 
					break;
				}
				
			    FragmentTransaction transaction = getFragmentManager().beginTransaction();
			    transaction.replace(R.id.fragment_adventry, newFragment);
			    transaction.addToBackStack(null);
			    transaction.setTransition(4099);	
			    transaction.commit(); 
			}
			
			
		});
		
		
		return advEntryView;
	}

}
