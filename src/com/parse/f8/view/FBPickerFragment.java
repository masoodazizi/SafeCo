package com.parse.f8.view;

import com.facebook.FacebookException;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.parse.f8.R;
import com.parse.f8.R.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

///**
// * A simple {@link Fragment} subclass.
// * @param <PickerActivity>
// * 
// */

public class FBPickerFragment extends Fragment {

	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	private FriendPickerFragment friendPickerFragment;
	
	public FBPickerFragment() {
		// Required empty public constructor
	}
//	private FragmentActivity mycontext;
//	public void onAttach(Activity activity) {
//		
//		mycontext = 
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fBPickerView = inflater.inflate(R.layout.fragment_fbpicker, container, false);
	    //Bundle args = getActivity().getIntent().getExtras();
	    
	   
	    
	    
	    
		
		return fBPickerView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 FragmentManager manager = getActivity().getSupportFragmentManager();
		    Fragment fragmentToShow = null;

		    //friendPickerFragment = new FriendPickerFragment();
		    friendPickerFragment =  (FriendPickerFragment) manager.findFragmentById(R.id.picker_fragment);
		    friendPickerFragment
		            .setOnErrorListener(new PickerFragment.OnErrorListener() {
		                @Override
		                public void onError(PickerFragment<?> fragment,
		                        FacebookException error) {
		                    Toast.makeText(getActivity(), error.getMessage(),
		                            Toast.LENGTH_SHORT).show();
		                }
		            });
		    friendPickerFragment
		            .setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
		                @Override
		                public void onDoneButtonClicked(PickerFragment<?> fragment) {
		                    Toast.makeText(getActivity(), "Done",
		                            Toast.LENGTH_SHORT).show();
		                }
		            });
		    
//		    friendPickerFragment.setUserId("1528617237397995");
		    String userId = friendPickerFragment.getUserId();
		    Log.d("MyDebug", "ID= " + userId);
		    
		    try {
		        friendPickerFragment.loadData(true);
		    } catch (Exception ex) {
		    	Log.d("Error_Debug", ex.toString());
		    }
		    
		    fragmentToShow = friendPickerFragment;

		    manager.beginTransaction()
		            .replace(R.id.picker_fragment, fragmentToShow).commit();
	}
	
	@Override
	public void onStart() {
	    super.onStart();
	    try {
	        friendPickerFragment.loadData(false);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
//	    	friendPickerFragment.loadData(false);
	    }
	}

	    
	
}




///////////////////////////////////////////////

//FragmentManager manager = getActivity().getSupportFragmentManager();
//Fragment fragmentToShow = null;
//Uri intentUri = getActivity().getIntent().getData();
//
//if (FRIEND_PICKER.equals(intentUri)) {
//    if (savedInstanceState == null) {
//        friendPickerFragment = new FriendPickerFragment(args);
//    } else {
//        friendPickerFragment = 
//            (FriendPickerFragment) manager.findFragmentById(R.id.picker_fragment);
//    }
//    // Set the listener to handle errors
//    friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
//        @Override
//        public void onError(PickerFragment<?> fragment,
//                            FacebookException error) {
//            onError(error);
//        }
//
//        
//        
//    });
//    // Set the listener to handle button clicks
//    friendPickerFragment.setOnDoneButtonClickedListener(
//            new PickerFragment.OnDoneButtonClickedListener() {
//        @Override
//        public void onDoneButtonClicked(PickerFragment<?> fragment) {
//            finishActivity();
//        }
//    });
//    fragmentToShow = friendPickerFragment;
//
//} else {
//    // Nothing to do, finish
//    getActivity().setResult(0);
//    getActivity().finish();
//    return fBPickerView;
//}
//
//manager.beginTransaction()
//       .replace(R.id.picker_fragment, fragmentToShow)
//       .commit();


	
	
	
	
//	public void onError(Exception error) {
//    onError(error.getLocalizedMessage(), false);
//}
//
//private void onError(String error, final boolean finishActivity) {
//    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//    builder.setTitle("Error").
//            setMessage(error).
//            setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if (finishActivity) {
//                        finishActivity();
//                    }
//                }
//            });
//    builder.show();
//}
//
//private void finishActivity() {
//    getActivity().setResult(-1, null);
//    getActivity().finish();
//}
