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

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.f8.R;

public class SignInActivity extends ActionBarActivity {

	public static final String USER_INFO_PREFS = "UserInfoPrefs";
	public static final String PARSE_ADV_PRIVACY_CLASS = "RestrictedList";
	public static final String PARSE_SIMPLE_PRIVACY_CLASS = "PrivacyProfile";
	private String profilePicPath = "";
	
//	private Boolean finishFlag;
	//	public SignInActivity() {
//		this.finishFlag = false;
//	}
	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		//////////////////////////
//		 try {
//		        PackageInfo info = getPackageManager().getPackageInfo("com.parse.f8", PackageManager.GET_SIGNATURES);
//		        for (android.content.pm.Signature signature : info.signatures) {
//		            MessageDigest md = MessageDigest.getInstance("SHA");
//		            md.update(signature.toByteArray());
//		            String sign=Base64.encodeToString(md.digest(), Base64.DEFAULT);
//		            Log.d("MY KEY HASH:", sign);
//		          //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
//		        }
//		} catch (NameNotFoundException e1) {
//		} catch (NoSuchAlgorithmException e1) {
//		}
		 //////////////////////////
		
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
		List<String> permissions = Arrays.asList("public_profile", "user_friends");
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
								ParseUser.getCurrentUser().put("name",
										user.getName());
								
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
								
								initializeRowInParseClass(userId, PARSE_ADV_PRIVACY_CLASS);
								initializeRowInParseClass(userId, PARSE_SIMPLE_PRIVACY_CLASS);
							    
//								ParseUser.getCurrentUser().put("birthday",
//										user.getBirthday());
//								ParseUser.getCurrentUser().put("location",
//										user.getLocation());
//								ParseUser.getCurrentUser().put("username",
//										user.getUsername());
								ParseUser.getCurrentUser().saveInBackground();
								finishActivity();
								
//								FinishFlag finishFlag = new FinishFlag(getApplicationContext());
//								if (finishFlag.getFinishFlag()) {
//								finishActivity();
//								} else {
//									finishFlag.setFinishFlag(true);
//								}
								
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

	public void finishActivity() {
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
        
		new ImageDownloader(getApplicationContext()).execute(profilePicPath, userId);		
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
//	    editor.putString("name" , user.getName().toString());
//	    editor.putString("firstName" , user.getFirstName().toString());
//	    editor.putString("lastName" , user.getLastName().toString());
////	    editor.putString("userName" , user.().toString());
//	    editor.putString("fbId", user.getId().toString());
	    
	    JSONObject json = new JSONObject();
	    json = user.getInnerJSONObject();
	    try {
			editor.putString("fbId" , json.getString("id"));
			editor.putString("name" , json.getString("name"));
			editor.putString("firstName" , json.getString("first_name"));
			editor.putString("gender" , json.getString("gender"));
			editor.putString("lastName" , json.getString("last_name"));
			editor.putString("link" , json.getString("link"));
			editor.putString("locale" , json.getString("locale"));
			editor.putString("timezone" , json.getString("timezone"));
			editor.putString("email" , json.getString("email"));
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("JSONError", "Data from JSON object could not be saved in shared preferences!");
		}
//		editor.putString(type , user.getInnerJSONObject().toString());
		//editor.putString("email", user.get());
		//editor.putString("name", user.toString());

		editor.commit();
		
	}
	
	private void initializeRowInParseClass(String userId, String parseClass) {
		
		Boolean userExist = false;
		ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClass);
		query.whereEqualTo("userId", userId);
		List<ParseObject> userObj=null;
		try {
			userObj = query.find();
		} catch (ParseException e1) {
			Log.d("ParseError", "Error: " + e1.getMessage());
			e1.printStackTrace();
		}
		if (userObj.size() != 0) {
			userExist = true;
		}
		
		if (!userExist) {
			ParseObject parseObj = new ParseObject(parseClass);
			parseObj.put("userId", userId);
			if (parseClass == PARSE_SIMPLE_PRIVACY_CLASS) {
				parseObj.put("profile", "normal");
			}
			parseObj.saveInBackground();
		}
	}
	
}



class ImageDownloader extends AsyncTask<String , Void, Bitmap> {

	Context ctx;
	String imgPath;
	String userId;
		public static final String PROFILE_PIC_PREF = "profilePicPrefs"; 
	
	public ImageDownloader(Context ctx, String... param) {
		this.ctx = ctx;
		this.userId = param[1];
	}

	
	@Override
	protected Bitmap doInBackground(String... param) {
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
		
//		SignInActivity signInActivity = new SignInActivity();
//		FinishFlag finishFlag = new FinishFlag(ctx);
//		if (finishFlag.getFinishFlag()) {
//			signInActivity.finishActivity();
//		} else {
//			finishFlag.setFinishFlag(true);
//		}

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
					
					// Save inputStream into a byte array to store on Parse
//					byte[] imgBytes = inputStreamToBytes(inputStream);
//					saveBytesInParse(imgBytes);					

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
	
	private byte[] inputStreamToBytes(InputStream is) throws IOException {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1048576];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
//		for (int len; (len = is.read(data)) != -1;) {
//		buffer.write(data, 0, len);
		buffer.flush();

		return buffer.toByteArray();
	}
	
	private void saveBytesInParse(byte[] imgBytes) {
		
		final ParseFile file = new ParseFile(userId+".jpg", imgBytes);
		file.saveInBackground();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
		query.whereEqualTo("fbID", userId);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> userObj, ParseException e) {
				
				if (e == null) {
				
					if (userObj == null || userObj.size()==0) {
	
						Log.d("ParseQueryError", "There is no user object with user ID " + userId + 
		        				" defined in <User> Parse Class");
	
		        	} else {
		        		
		        		ParseObject user = userObj.get(0);
		        		user.put("profileImage", file);
		        		user.saveInBackground();
		        	}
					
				} else {
					Log.d("ParseError", "Error: " + e.getMessage());
				}
			}
		});
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




//class FinishFlag {
//
//	public static final String FINISH_FLAG_PREFS = "FinishFlagPrefs";
//	private Boolean finishFlag;
//	Context ctx;
//	SharedPreferences finishFlagPrefs;
//
//	public FinishFlag (Context context) {
//		
//		this.ctx = context;
//		this.finishFlagPrefs = ctx.getSharedPreferences(FINISH_FLAG_PREFS, 0);
//	}
//		
//	public Boolean getFinishFlag () {
//		
//		this.finishFlag = finishFlagPrefs.getBoolean("finishFlag", false);
//		return this.finishFlag;
//	}
//	
//	public void setFinishFlag(Boolean finishTag) {
//
//		SharedPreferences.Editor editor = finishFlagPrefs.edit();
//		editor.putBoolean("finishFlag", finishTag);
//		editor.commit();
//		this.finishFlag = finishTag;
//	}
//}




//SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("main",getApplicationContext().MODE_PRIVATE);
//SharedPreferences.Editor editor = sharedPref.edit();

//SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

//SettingFragment.getProfilePictureView().setProfileId(user.getId());
//SettingFragment.getUserNameView().setText(user.getName());
//SettingFragment.userNameView.setText("Jalal Khademi");



//private String saveToInternalSorage(Bitmap bitmapImage){
//    ContextWrapper cw = new ContextWrapper(getApplicationContext());
//     // path to /data/data/yourapp/app_data/imageDir
//    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//    // Create imageDir
//    File mypath=new File(directory,"profile.jpg");
//
//    FileOutputStream fos = null;
//    try {           
//
//        fos = new FileOutputStream(mypath);
//
//   // Use the compress method on the BitMap object to write image to the OutputStream
//        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        fos.close();
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//    return directory.getAbsolutePath();
//}