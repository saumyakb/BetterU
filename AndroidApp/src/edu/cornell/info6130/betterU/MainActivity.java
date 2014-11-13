package edu.cornell.info6130.betterU;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;


// TODO: consider defining request codes in values, to distinguish between request types/actions?

public class MainActivity extends ActionBarActivity {
	private static int SURVEY_REMINDER_REQUESTCODE = 1234567; 
//	private Bitmap bitmap;
//	private int lastImageRef;
	private AlarmManager		amReminder;
	private SharedPreferences 	appPreferences;
	private String LOG_TAG = "MainActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		// Always call the superclass first
        super.onCreate(savedInstanceState);
        
        // set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        // get handle to user's preferences for this app
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);	
        
        if (savedInstanceState != null) {
//        	ImageView ivw = (ImageView) findViewById(R.id.preview);
//        	
//        	ivw.setImageBitmap((Bitmap) savedInstanceState.getParcelable("background"));
//        	savedInstanceState.putParcelable("background",  null);
        }
        
        //
        setContentView(R.layout.activity_main);

        Button buttonSetWallpaper = (Button) findViewById(R.id.set);
        Button buttonResetWallpaper = (Button) findViewById(R.id.reset);
        
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        final Bitmap bitmap = ((BitmapDrawable)wallpaperDrawable).getBitmap();
        
        buttonSetWallpaper.setOnClickListener(new OnClickListener(){
        	 public void onClick(View v){
        		 WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        		 try {
        			 ImageView ivw = (ImageView) findViewById(R.id.preview);
        			 
        			 Bitmap bm = ((BitmapDrawable)ivw.getDrawable()).getBitmap();
        			 myWallpaperManager.setBitmap(bm);
        			 Toast.makeText(getApplicationContext(), "Wallpaper set sucessfully" ,Toast.LENGTH_SHORT).show();
        		 } catch(Exception e){
        			 Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        		 }
        	 }
        });
	
        buttonResetWallpaper.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		try {
        			wallpaperManager.setBitmap(bitmap);
        			Toast.makeText(getApplicationContext(),  "Original Wallpaper set successfully", Toast.LENGTH_LONG).show();
        		} catch(Exception e){
        			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        		}
        	}
        });
        
        // register shared preference listener to handle when user changes settings
        appPreferences.registerOnSharedPreferenceChangeListener( new SharedPreferences.OnSharedPreferenceChangeListener() {
        	@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        		try {
					// debug output
	        		switch (key) {        			
	        			case "pref_reminder_key":
	        			case "pref_bodyclock_sleep_key":
	                		if (BuildConfig.DEBUG){                 			
	                			Log.d(LOG_TAG + ".onSharedPreferenceChanged", String.valueOf(sharedPreferences.getBoolean("pref_reminder_key", true)));
	                			Log.d(LOG_TAG + ".onSharedPreferenceChanged", sharedPreferences.getString("pref_bodyclock_sleep_key", ""));
	                		}		
	                		// toggling reminders or sleep time should reset notification logic
	        				RegisterReminderBroadcast();
	        				break;
	        			default:	        				
	                		if (BuildConfig.DEBUG){
	                			// NOTE: code below will throw error on hash-sets, etc. (since it assumes a String value)
	                			Log.d(LOG_TAG + ".onSharedPreferenceChanged", key + "=" + sharedPreferences.getString(key, ""));
	                		}				
	        				break;
	        		}
            	} catch (Exception ex) {
            		if (BuildConfig.DEBUG){ 
            			Log.e(LOG_TAG + ".onSharedPreferenceChanged", ex.toString(), ex);
            		}
            	}
			}
		});
        
        amReminder = (AlarmManager) getSystemService(ALARM_SERVICE);
        RegisterReminderBroadcast();
        
        if (BuildConfig.DEBUG) {
          Log.v(LOG_TAG + ".onCreate", "Complete");
        } 
	}
	
	@Override
	protected void onSaveInstanceState(Bundle toSave) {		
		// TODO: add compression logic, will fail if bitmap > 1 MB
		
		// put image from imageView into storage (e.g., orientation change, etc)
//		ImageView ivw = (ImageView) findViewById(R.id.preview);
//		ivw.buildDrawingCache();
//		Parcelable bm = ivw.getDrawingCache();
		
//		toSave.putParcelable("background",  bm);
		
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(toSave);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// use this to reference preferences set by user
		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);		
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        // hide diagnostics menu from release build
        if(!BuildConfig.DEBUG) {
        	// hide email menu
        	menu.findItem(R.id.menu_email).setVisible(false);
        	// hide diagnostics/debug menu
        	menu.findItem(R.id.menu_debug).setVisible(false);
        }

        return true;
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
        		Intent email = new Intent(Intent.ACTION_SEND);
        		email.setType("message/rfc822");
        		email.putExtra(Intent.EXTRA_EMAIL,  new String[]{"curtis.josey@gmail.com"});
        		email.putExtra(Intent.EXTRA_SUBJECT,  "betterU Diagnostics");
        		email.putExtra(Intent.EXTRA_TEXT,  "Can you read this now?");
        		// email.setData(Uri.parse("mailto:" + "curtis.josey@gmail.com");
        		// revert back to our app after sending (instead of email app)
        		email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		try {
        			startActivity(Intent.createChooser(email,  "Send mail..."));        			
        		} catch (Exception exMail) {
        			Toast.makeText(this,  "There are no email clients installed.",  Toast.LENGTH_SHORT).show();
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
	    	
	    	intent.putExtra("alarm_message", getResources().getString(R.string.reminder_survey_alert));
	    	intent.putExtra("alarm_url", getResources().getString(R.string.uri_survey_daily));
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
    
//    private void UnregisterReminderBroadcast() {
  //  	alarmManager.cancel(piSurveyReminder);
//    	getBaseContext().unregisterReceiver(rxBroadcast);
    //}
    
	@Override
	public void onDestroy() {
//		unregisterReceiver(rxBroadcast);
		super.onDestroy();		
	}
}
