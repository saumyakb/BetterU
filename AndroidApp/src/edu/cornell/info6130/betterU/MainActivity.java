package edu.cornell.info6130.betterU;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnSharedPreferenceChangeListener {
	private final String LOG_TAG = MainActivity.class.getName();
	private static int SURVEY_REMINDER_REQUESTCODE = 1234567; 
	private static int PRIMING_REMINDER_REQUESTCODE = 7654321; 

	//	private Bitmap bitmap;
//	private int lastImageRef;
	private AlarmManager		amReminder;
	private SharedPreferences 	appPreferences;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		try {
			// Always call the superclass first
	        super.onCreate(savedInstanceState);
	        
	        // set default preferences
	        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
	        // get handle to user's preferences for this app
	        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);	
	        
	        //
	        setContentView(R.layout.activity_main);
	
	        // NOTE: consider pushing this into an AsynchTask and store on SD card instead
	        // NOTE: un-tested with "live" wallpaper
	        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
	        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
	        final Bitmap bitmap = ((BitmapDrawable)wallpaperDrawable).getBitmap();
	 
	//        if (BuildConfig.DEBUG) {
	//        	Log.d(LOG_TAG + ".onCreate", "bitmap size: " + String.valueOf(byteSizeOf(bitmap)));
	//        }
	        
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	        byte[] byteArray = stream.toByteArray();
//	        // free the bitmaps!!
//	        bitmap.recycle();
	        
	    	String imageEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
	
	    	// a memory "brake" added to prevent out of memory errors with very large images user may have set as default wallpaper
	    	if (byteArray.length <= 1500000) {
	    		// if image isn't too large, preserve copy to restore after priming window has passed
	    		SharedPreferences.Editor editor = appPreferences.edit();
	               editor.putString("imagePreferance", imageEncoded);
	               editor.commit();
	    	}
	                
	        // register shared preference listener to handle when user changes settings
	        appPreferences.registerOnSharedPreferenceChangeListener(this);
	        
	        amReminder = (AlarmManager) getSystemService(ALARM_SERVICE);
	        RegisterReminderBroadcast();
	        RegisterWallpaperBroadcast();
	        
	        if (BuildConfig.DEBUG) {
	          Log.v(LOG_TAG + ".onCreate", "Complete");
	        } 
		} catch (Exception ex) {
			if (BuildConfig.DEBUG){ 
				Log.e(LOG_TAG + ".onCreate", ex.toString(), ex);
			}
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		try {
			// debug output
    		switch (key) {
    			case "pref_participant_key":
    			case "pref_reminder_key":
    			case "pref_bodyclock_sleep_key":
//            		if (BuildConfig.DEBUG){                 			
//            			Log.d(LOG_TAG + ".onSharedPreferenceChanged", String.valueOf(sharedPreferences.getBoolean("pref_reminder_key", true)));
//            			Log.d(LOG_TAG + ".onSharedPreferenceChanged", sharedPreferences.getString("pref_bodyclock_sleep_key", ""));
//            		}		
            		// toggling reminders or sleep time should reset notification logic
    				RegisterReminderBroadcast();	        				
    				break;
    			default:	        				
//            		if (BuildConfig.DEBUG){
//            			// NOTE: code below will throw error on hash-sets, etc. (since it assumes a String value)
//            			Log.d(LOG_TAG + ".onSharedPreferenceChanged", key + "=" + sharedPreferences.getString(key, ""));
//            		}				
    				RegisterWallpaperBroadcast();
    				break;
    		}
    	} catch (Exception ex) {
    		if (BuildConfig.DEBUG){ 
    			Log.e(LOG_TAG + ".onSharedPreferenceChanged", ex.toString(), ex);
    		}
    	}
	}

	
	@Override
	protected void onSaveInstanceState(Bundle toSave) {		
		try {
			// TODO: add compression logic, will fail if bitmap > 1 MB
			
			// put image from imageView into storage (e.g., orientation change, etc)
	//		ImageView ivw = (ImageView) findViewById(R.id.preview);
	//		ivw.buildDrawingCache();
	//		Parcelable bm = ivw.getDrawingCache();
			
	//		toSave.putParcelable("background",  bm);
			
			// Always call the superclass so it can save the view hierarchy state
			super.onSaveInstanceState(toSave);
		} catch (Exception ex) {
			if (BuildConfig.DEBUG){ 
				Log.e(LOG_TAG + ".onSaveInstanceState", ex.toString(), ex);
			}
		}
	}
	
	@Override
	public void onResume() {
		try {
			super.onResume();
			// use this to reference preferences set by user
			appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	        // register shared preference listener to handle when user changes settings
	        appPreferences.registerOnSharedPreferenceChangeListener(this);
		} catch (Exception ex) {
			if (BuildConfig.DEBUG){ 
				Log.e(LOG_TAG + ".onResume", ex.toString(), ex);
			}
		}

	}

	@Override
	public void onPause() {
		try {
			super.onPause();
	        // register shared preference listener to handle when user changes settings
	        appPreferences.unregisterOnSharedPreferenceChangeListener(this);
		} catch (Exception ex) {
			if (BuildConfig.DEBUG){ 
				Log.e(LOG_TAG + ".onPause", ex.toString(), ex);
			}
		}	        
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	try {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        
	        // hide diagnostics menu from release build
	        if(!BuildConfig.DEBUG) {
	//        	// hide email menu
	//        	menu.findItem(R.id.menu_email).setVisible(false);
	        	// hide diagnostics/debug menu
	        	menu.findItem(R.id.menu_debug).setVisible(false);
	        	// hide view log menu
	        	menu.findItem(R.id.menu_log).setVisible(false);
	        }
	
	        return true;
		} catch (Exception ex) {
			if (BuildConfig.DEBUG){ 
				Log.e(LOG_TAG + ".onCreateOptionsMenu", ex.toString(), ex);
			}
			return false;
		}       
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	// used solely for testing new features or debugging existing features 'on command'
        	case R.id.menu_debug:
        		try {
        			Context context = getApplicationContext();
//    				Toast.makeText(context,  "Feature not available.",  Toast.LENGTH_SHORT).show();
        			
        			// get selected food types
        			Set<String> foodPlate = appPreferences.getStringSet("pref_food_list_key", null);
        			Set<String> mealTimes = appPreferences.getStringSet("pref_meal_list_key", null);
//        			Set<String> snackTimes = appPreferences.getStringSet("pref_snack_list_key", null);

        			// get next priming photo
        			PhotoManager iPhoto = new PhotoManager(context);
        			Drawable displayImage = iPhoto.getPrimingPhoto(foodPlate, mealTimes);
        			
        			// get handle to object used to display image
        			ImageView img = (ImageView) findViewById(R.id.preview);
        			img.setScaleType(ScaleType.FIT_CENTER);
        			                    
                    if (displayImage == null) {                    	
                    	img.setImageDrawable(getResources().getDrawable(R.drawable.bg_plain2));
                    } else {
                    	// set image to ImageView
                    	img.setImageDrawable(displayImage);
                    }
                    
        		} catch (Exception ex2) {
        			if (BuildConfig.DEBUG) {
        				Log.e(LOG_TAG + ".onOptionsItemSelected", ex2.toString(), ex2);
        			}
        		}
        		
        		break;
        	case R.id.menu_email:
        		String[] 	email_to = new String[]{"curtis.josey@gmail.com"};
        		String 		email_subject = "betterU Diagnostics";
        		String 		email_body = "";

        		// participant id (if not set, returns UNKNOWN)
        		email_body = "ParticipantID: " + appPreferences.getString("pref_participant_key", "UNKNOWN") + System.getProperty("line.separator");
        		// get Device model
        		email_body += "Device: " + Build.MODEL + System.getProperty("line.separator");
        		// get Android version
        		email_body += "OS: " + Build.VERSION.RELEASE + System.getProperty("line.separator");
        		// dump priming log
        		email_body += System.getProperty("line.separator") + "Priming Log:" + System.getProperty("line.separator");
    			DatabaseLogger logger = new DatabaseLogger(getApplicationContext());
				logger.open();
				email_body += logger.getAll().toString();
				logger.close();
        		
        		Intent email = new Intent(Intent.ACTION_SEND);

        		email.setType("message/rfc822");
        		email.putExtra(Intent.EXTRA_EMAIL, email_to);        		
        		email.putExtra(Intent.EXTRA_SUBJECT, email_subject);        		
        		email.putExtra(Intent.EXTRA_TEXT,  email_body);
        		// revert back to our app after sending (instead of email app)
        		email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		
        		try {
        			startActivity(Intent.createChooser(email,  "Send mail..."));        			
        		} catch (Exception exMail) {
        			Toast.makeText(this,  "There are no email clients installed.",  Toast.LENGTH_SHORT).show();
        		}
        		break;
        	case R.id.menu_log:
        		if (BuildConfig.DEBUG) {
        			DatabaseLogger debugLogger = new DatabaseLogger(getApplicationContext()); 
        		
        		 	debugLogger.open();
        		 	
        			try {    				
        				Log.d(LOG_TAG + ".onOptionsItemSelected", "Log Dump\r\n: " + debugLogger.getAll().toString());
        			}
        			catch (Exception el) {
        				Log.w(LOG_TAG + ".onOptionsItemSelected", el.toString(), el);
        				Log.w(LOG_TAG + ".onOptionsItemSelected", "calling deleteAll Logs");
        				debugLogger.deleteAll();
        			} finally {    				
        				debugLogger.close();
        			}
        		}
        		break;
        	case R.id.menu_settings:
	        	//open settings;
	        	Intent intent = new Intent(this, PrefActivity.class);
	        	startActivity(intent);
	
	        	break;
        	// TODO: add &ParticipantID= and the SharedPreference ParticipantID (if value is set)
	        case R.id.menu_survey_daily:
	        	// get path to survey
	        	String surveyPathDaily = getResources().getString(R.string.uri_survey_daily);
	        	// load survey page URL in a browser window
	        	ShowSurvey(surveyPathDaily);
	        	break;
	        case R.id.menu_survey_initial:
	        	// get path to survey
	        	String surveyPathFirst = getResources().getString(R.string.uri_survey_first);
	        	// load survey page URL in a browser window
	        	ShowSurvey(surveyPathFirst);
	        	break;
	        case R.id.menu_survey_final:	        	
	        	// get path to survey
	        	String surveyPathFinal = getResources().getString(R.string.uri_survey_final);
	        	// load survey page URL in a browser window
	        	ShowSurvey(surveyPathFinal);
	        	break;
	        default:
	        	// home & up buttons; as you specify a parent activity in AndroidManifest.xml.
	            return super.onOptionsItemSelected(item);
        }        
        
        return true;
    }
    
    private void ShowSurvey(String surveyPath) {
    	// set user expectations
    	Toast.makeText(this, "Opening Survey...", Toast.LENGTH_SHORT).show();

    	try {
    		String uriParm = "&ParticipantID=";
    		String participantID = appPreferences.getString("pref_participant_key", "");
    		
    		if (participantID.length() != 0) {
    			surveyPath += uriParm + participantID;
    		}
    		
    		if (BuildConfig.DEBUG) {
    			Log.v(LOG_TAG + ".ShowSurvey", surveyPath);
    		}
    		
    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(surveyPath));
    		startActivity(browserIntent);
    	} 
    	catch (Exception ew) {
    		// surveyPath
    		Toast.makeText(this, "Failed to load survey:\r\n" + surveyPath, Toast.LENGTH_LONG).show();
    		// Toast.makeText(this, ew.toString(), Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void RegisterReminderBroadcast() {
    	try {
    		long alarmInterval = AlarmManager.INTERVAL_DAY;
	    	Intent intent = new Intent(this, ReminderReceiver.class);
	    	
	    	Bundle bundle=new Bundle();
		    bundle.putString("alarm_message", getResources().getString(R.string.reminder_survey_alert));
	    	
	    	String surveyPath = getResources().getString(R.string.uri_survey_daily);
	    	String uriParm = "&ParticipantID=";
    		String participantID = appPreferences.getString("pref_participant_key", "");
    		
    		if (participantID.length() != 0) {
    			surveyPath += uriParm + participantID;
    		}    		
		    bundle.putString("alarm_url",surveyPath);
		    intent.putExtras(bundle);
	    	// cancel any existing reminder
	    	PendingIntent pendingIntent = PendingIntent.getBroadcast(this, SURVEY_REMINDER_REQUESTCODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    	
	    	if (appPreferences.getBoolean("pref_reminder_key", true)) {
		    	// if debug, set for 1 minute delay ... otherwise calculate time based on user preference
		    	Calendar cal = Calendar.getInstance();
		    	// start with system time
		    	cal.setTimeInMillis(System.currentTimeMillis());
		    	// get user specified time for bed (default to 21)
		    	String tempSleep = appPreferences.getString("pref_bodyclock_sleep_key", getResources().getString(R.string.pref_bodyclock_sleep_default));
		    	// convert to int
		    	Integer sleepTime = Integer.valueOf(tempSleep);
		    	
		    	if (BuildConfig.DEBUG) {
		    		// trigger immediately
		    		cal.add(Calendar.MINUTE, 1);
		    		// override default interval from one day to 1 minute (for testing)
		    		alarmInterval = 60000;
		    	} else {
			    	// start with sleep time
			    	cal.set(Calendar.HOUR_OF_DAY, sleepTime);		
		    		// turn clock back by 3 hours
		    		cal.add(Calendar.HOUR_OF_DAY, -3);
		    	}
		    	if (BuildConfig.DEBUG) {
		    		// dump next alarm time
		    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", getResources().getConfiguration().locale);
		    		// sdf.setTimeZone(TimeZone.getDefault());
		    		Log.d(LOG_TAG + ".RegisterReminderBroadcast", sdf.format(cal.getTime()));
		    	}

		    	pendingIntent = PendingIntent.getBroadcast(this, SURVEY_REMINDER_REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		    	// set alarm
		    	// amReminder.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmInterval, pendingIntent);
		    	amReminder.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmInterval, pendingIntent);
		    	if (BuildConfig.DEBUG) {
		    		Log.d(LOG_TAG + ".RegisterReminderBroadcast", "Reminder Scheduled");
		    	}		    	
	    	} else {
		    	//PendingIntent pendingIntent = PendingIntent.getBroadcast(this, SURVEY_REMINDER_REQUESTCODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		    	amReminder.cancel(pendingIntent);
		    	
	    		if (BuildConfig.DEBUG){ 
	    			Log.d(LOG_TAG + ".RegisterReminderBroadcast", "User has Reminders disabled.");
	    		}
	    	}
	    	    	
    	} catch (Exception ex) {
    		// StackTraceElement trace = new Exception().getStackTrace();
    		Toast.makeText(this,  "RegisterReminderBroadcast:\r\n" + ex.toString(),  Toast.LENGTH_LONG).show();
    		if (BuildConfig.DEBUG){ 
    			Log.e(LOG_TAG + ".RegisterReminderBroadcast", ex.toString(), ex);
    		}
    	}
    }

    private void RegisterWallpaperBroadcast() {
    	try {    	
    		  		
	    	Set<String> foodPlate = appPreferences.getStringSet("pref_food_list_key", null);
			Set<String> mealTimes = appPreferences.getStringSet("pref_meal_list_key", null);
	        //intent to set a wallpaper 
	        Log.d("set Intent ", "first intent");
	        
		    Intent myIntent = new Intent(this, setWallpaper.class);
		    String[] foodPlateStrArr = foodPlate.toArray(new String[foodPlate.size()]);
		    String[] mealTimesStrArr = mealTimes.toArray(new String[mealTimes.size()]);
		    
		    Log.d("foodPlateStrArr Main ACt",foodPlateStrArr[0]);
	    	Log.d("mealTimesStrArr Main Act",mealTimesStrArr[0]);
	    	
		    Bundle bundle=new Bundle();
		    bundle.putStringArray("foodPlateBundle", foodPlateStrArr);
		    bundle.putStringArray("mealTimesBundle", mealTimesStrArr);
		    myIntent.putExtras(bundle);// myIntent.putExtras(bundleMealTimes);
		    
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, PRIMING_REMINDER_REQUESTCODE, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		    
		    Calendar cal = Calendar.getInstance();
		    // set timer to start at the start of next half hour
		    int unroundedMinutes = cal.get(Calendar.MINUTE);
		    int mod = 60 - unroundedMinutes % 60;
		    long setWallpaper = cal.getTimeInMillis()+ mod *60000;
		    
		    
	    	if (BuildConfig.DEBUG) {
	    		// dump next alarm time
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", getResources().getConfiguration().locale);
	    		// sdf.setTimeZone(TimeZone.getDefault());
	    		Log.d(LOG_TAG + ".RegisterWallpaperBroadcast(to set)", sdf.format(setWallpaper));
	    	}
		   	      
			// AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			Log.d("set Alarm ", "first alarm");
			amReminder.setRepeating(AlarmManager.RTC_WAKEUP, setWallpaper, 3600000, pendingIntent);
    	} catch (Exception ex) {
    		// StackTraceElement trace = new Exception().getStackTrace();
//    		Toast.makeText(this,  "RegisterWallpaperBroadcast:\r\n" + ex.toString(),  Toast.LENGTH_LONG).show();
    		if (BuildConfig.DEBUG){ 
    			Log.e(LOG_TAG + ".RegisterWallpaperBroadcast", ex.toString(), ex);
    		}
    	}

    }
    
    // this function returns estimated size (so we can see if it'll fit in app preferences
    // based on code from: stackoverflow.com/questions/2407565/bitmap-byte-size-after-decoding
    @SuppressLint("NewApi")
    private static int byteSizeOf(Bitmap bitmap) {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    		return bitmap.getAllocationByteCount();
    	} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
    		return bitmap.getByteCount();
    	} else {
    		return bitmap.getRowBytes() & bitmap.getHeight();
    	}
    }
    
//    private void UnregisterReminderBroadcast() {
  //  	alarmManager.cancel(piSurveyReminder);
//    	getBaseContext().unregisterReceiver(rxBroadcast);
    //}
    
	@Override
	public void onDestroy() {
		try {
	        // register shared preference listener to handle when user changes settings
	        appPreferences.unregisterOnSharedPreferenceChangeListener(this);
			//		unregisterReceiver(rxBroadcast);
			super.onDestroy();		
		} catch (Exception ex) {
			if (BuildConfig.DEBUG){ 
				Log.e(LOG_TAG + ".onDestroy", ex.toString(), ex);
			}
		}
	}
}
