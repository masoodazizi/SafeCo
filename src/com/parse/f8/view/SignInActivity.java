/**
 * Copyright 2014 Facebook, Inc.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

package com.parse.f8.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.string;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.FbDialog;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.f8.R;

public class SignInActivity extends ActionBarActivity {

	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	private String profilePicPath = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		TextView tos = (TextView) findViewById(R.id.tos);
		tos.setMovementMethod(LinkMovementMethod.getInstance());

		Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		List<String> permissions = Arrays.asList("public_profile");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user != null) {
					if (user.isNew()) {
						// set favorites as null, or mark it as empty somehow
						makeMeRequest();
					} else {
						finishActivity();
					}
				}
			}
		});
	}

	private void makeMeRequest() {
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			Request request = Request.newMeRequest(
					ParseFacebookUtils.getSession(),
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								ParseUser.getCurrentUser().put("fbID",
										user.getId());
								ParseUser.getCurrentUser().put("firstName",
										user.getFirstName());
								ParseUser.getCurrentUser().put("lastName",
										user.getLastName());
								
//								try {
//		                            URL imgUrl = new URL("http://graph.facebook.com/"
//		                                    + user.getId() + "/picture?type=large");
//		                            profilePicPath = imgUrl.toString();
//
//		                            InputStream in = (InputStream) imgUrl.getContent();
//		                            Bitmap fbBitmap = BitmapFactory.decodeStream(in);
//		                            //Bitmap bitmap = BitmapFactory.decodeStream(imgUrl      // tried this also
//		                            //.openConnection().getInputStream());
//		                            profilePicPath = saveToInternalSorage(fbBitmap);
//		                        } catch (Exception e) {
//		                            e.printStackTrace();
//		                        }
								
								String userId = user.getId();
								saveProfilePhoto(userId);
								
								saveUserInfoPref(user);
							    
//								ParseUser.getCurrentUser().put("birthday",
//										user.getBirthday());
//								ParseUser.getCurrentUser().put("location",
//										user.getLocation());
//								ParseUser.getCurrentUser().put("username",
//										user.getUsername());
								ParseUser.getCurrentUser().saveInBackground();
								finishActivity();
							} else if (response.getError() != null) {
								if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
										|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
									Toast.makeText(getApplicationContext(),
											R.string.session_invalid_error,
											Toast.LENGTH_LONG).show();

								} else {
									Toast.makeText(getApplicationContext(),
											R.string.logn_generic_error,
											Toast.LENGTH_LONG).show();
								}
							}
						}
					});
			request.executeAsync();

		}
	}

	private void finishActivity() {
		// Start an intent for the dispatch activity
		Intent intent = new Intent(SignInActivity.this, DispatchActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	private void saveProfilePhoto(String userId) {
		
		URL imgUrl = null;
		try {
			imgUrl = new URL("http://graph.facebook.com/"
			        + userId + "/picture?type=large");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        profilePicPath = imgUrl.toString();
//        String fileName = "profile.jpg";
//        File imageFilePath = generateImagePath(fileName);
        
		new ImageDownloader(getApplicationContext()).execute(profilePicPath);		
	}

	// FIXME Delay to load profile data in profile fragment, makes empty values.
	
//	private File generateImagePath(String fileName) {
//		
//		ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/app-name/app_data/imageDir
//       File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//       // Create imageDir
//       File imageFilePath=new File(directory, fileName);
//		
//		return imageFilePath;
//	}
	
	private void saveUserInfoPref(GraphUser user) {
		
		SharedPreferences userInfoPref = getSharedPreferences(USER_INFO_PREFS, 0);
	    SharedPreferences.Editor editor = userInfoPref.edit();
	    editor.putString("name" , user.getName().toString());
	    editor.putString("fbId", user.getId().toString());
	    
//		editor.putString(type , user.getInnerJSONObject().toString());
		//editor.putString("email", user.get());
		//editor.putString("name", user.toString());
		editor.commit();
		
	}
	
	
//	private String saveToInternalSorage(Bitmap bitmapImage){
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//         // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory,"profile.jpg");
//
//        FileOutputStream fos = null;
//        try {           
//
//            fos = new FileOutputStream(mypath);
//
//       // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return directory.getAbsolutePath();
//    }
	
}

class ImageDownloader extends AsyncTask<String , Void, Bitmap> {

	Context ctx;
	String imgPath;
	public static final String PROFILE_PIC_PREF = "profilePicPrefs"; 
	
	public ImageDownloader(Context ctx) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
	}
	
	@Override
	protected Bitmap doInBackground(String... param) {
		// TODO Auto-generated method stub
		Log.i("Async", "doInBackground Called");
		return downloadBitmap(param[0]);
	}

	@Override
	protected void onPreExecute() {
		Log.i("Async", "onPreExecute Called");

	}

	protected void onPostExecute(Bitmap result) {
		Log.i("Async", "onPostExecute Called");
		//downloadedImg.setImageBitmap(result);
		//simpleWaitDialog.dismiss();
		imgPath = saveToInternalSorage(result);
		savePathPref(imgPath, PROFILE_PIC_PREF);

	}

	private Bitmap downloadBitmap(String url) {
		// initilize the default HTTP client object
		final DefaultHttpClient client = new DefaultHttpClient();

		//forming a HttoGet request 
		final HttpGet getRequest = new HttpGet(url);
		try {

			HttpResponse response = client.execute(getRequest);

			//check 200 OK for success
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode + 
						" while retrieving bitmap from " + url);
				return null;

			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					// getting contents from the stream 
					inputStream = entity.getContent();

					// decoding stream data back into image Bitmap that android understands
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// You Could provide a more explicit error message for IOException
			getRequest.abort();
			Log.e("ImageDownloader", "Something went wrong while" +
					" retrieving bitmap from " + url + e.toString());
		} 

		return null;
	}

	private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(ctx);
         // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {           

            fos = new FileOutputStream(mypath);

       // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

	private void savePathPref(String imgPath, String PROFILE_PIC_PREF){
		
		SharedPreferences profileImgPath = ctx.getSharedPreferences(PROFILE_PIC_PREF, 0);
		SharedPreferences.Editor editor = profileImgPath.edit();
		editor.putString("imgPath", imgPath);
		editor.commit();
	}
	
}




//SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("main",getApplicationContext().MODE_PRIVATE);
//SharedPreferences.Editor editor = sharedPref.edit();

//SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

//SettingFragment.getProfilePictureView().setProfileId(user.getId());
//SettingFragment.getUserNameView().setText(user.getName());
//SettingFragment.userNameView.setText("Jalal Khademi");