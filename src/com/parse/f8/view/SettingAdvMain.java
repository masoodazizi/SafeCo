package com.parse.f8.view;

import com.parse.f8.R;
import com.parse.f8.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link SettingAdvMain.OnFragmentInteractionListener}
 * interface to handle interaction events.
 * 
 */
public class SettingAdvMain extends Fragment {
//  TASK Try to implement fragments extending ListFragment!
	
// 	private OnFragmentInteractionListener mListener;
	String[] advMainListItems;
	
	public SettingAdvMain() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View mainAdvView = inflater.inflate(R.layout.setting_advmain, container, false);
		
		advMainListItems = getResources().getStringArray(R.array.advsetting_list_items);
		ListView mainAdvSettingList = (ListView) mainAdvView.findViewById(R.id.listAdvMain);
		ListAdapter mainAdvSettingAdapter = new ArrayAdapter<String>
					(getActivity(), R.layout.entry_item_advmain, R.id.lbl_entryItem_advmain, advMainListItems);
		mainAdvSettingList.setAdapter(mainAdvSettingAdapter);
		
		mainAdvSettingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Fragment newFragment = null;
//				Log.d("myDebug", "Selected Item:" + Integer.toString(position) );
				
				switch (position) {
				case 0:
					newFragment = new SettingAdvEntry();
					break;
				case 1:
					newFragment = new SettingAdvEntry();
					break;
				}
				
			    FragmentTransaction transaction = getFragmentManager().beginTransaction();
			    transaction.replace(R.id.fragment_advmain, newFragment);
			    transaction.addToBackStack(null);
			    transaction.setTransition(4099);
			    transaction.commit(); 
			}
			
			
		});
		
				
		return mainAdvView;
	}

	
	
}	
	
//	// TODO: Rename method, update argument and hook method into UI event
//	public void onButtonPressed(Uri uri) {
//		if (mListener != null) {
//			mListener.onFragmentInteraction(uri);
//		}
//	}
//
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		try {
//			mListener = (OnFragmentInteractionListener) activity;
//		} catch (ClassCastException e) {
//			throw new ClassCastException(activity.toString()
//					+ " must implement OnFragmentInteractionListener");
//		}
//	}
//
//	@Override
//	public void onDetach() {
//		super.onDetach();
//		mListener = null;
//	}
//
//	/**
//	 * This interface must be implemented by activities that contain this
//	 * fragment to allow an interaction in this fragment to be communicated to
//	 * the activity and potentially other fragments contained in that activity.
//	 * <p>
//	 * See the Android Training lesson <a href=
//	 * "http://developer.android.com/training/basics/fragments/communicating.html"
//	 * >Communicating with Other Fragments</a> for more information.
//	 */
//	public interface OnFragmentInteractionListener {
//		// TODO: Update argument type and name
//		public void onFragmentInteraction(Uri uri);
//	}
//
//}
