package edu.cornell.info6130.betterU;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

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
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


// TODO: consider defining request codes in values, to distinguish between request types/actions?

public class MainActivity extends ActionBarActivity {
	private static int SURVEY_REMINDER_REQUESTCODE = 1234567; 
	private Bitmap bitmap;
	private int lastImageRef;
	private AlarmManager		amReminder;
	private SharedPreferences 	appPreferences;
	private String LOG_TAG = "MainActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        // get handle to user's preferences for this app
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);	
        //
        setContentView(R.layout.activity_main);
        
        Button buttonSetWallpaper = (Button) findViewById(R.id.set);
        Button buttonResetWallpaper = (Button) findViewById(R.id.reset);
        ImageView imagePreview = (ImageView) findViewById(R.id.preview);
        
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        final Bitmap bitmap = ((BitmapDrawable)wallpaperDrawable).getBitmap();
        
        buttonSetWallpaper.setOnClickListener(new OnClickListener(){
        	 public void onClick(View v){
        		 WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        		 try {
        			 myWallpaperManager.setResource(R.drawable.salad);
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
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
        		try {
					// debug output
	        		switch (key) {        			
	        			case "pref_reminder_key":
	        			case "pref_bodyclock_sleep_key":
	                		if (BuildConfig.DEBUG){                 			
	                			Log.d(LOG_TAG + ".onSharedPreferenceChanged", String.valueOf(appPreferences.getBoolean("pref_reminder_key", true)));
	                			Log.d(LOG_TAG + ".onSharedPreferenceChanged", appPreferences.getString("pref_bodyclock_sleep_key", ""));
	                		}		
	                		// toggling reminders or sleep time should reset notification logic
	        				RegisterReminderBroadcast();
	        				break;
	        			default:
	                		if (BuildConfig.DEBUG){ 
	                			Log.d(LOG_TAG + ".onSharedPreferenceChanged", key + "=" + appPreferences.getString(key, ""));
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
	public void onResume() {
		super.onResume();
		// use this to reference preferences set by user
		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);		
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	// used solely for testing new features or debugging existing features 'on command'
        	case R.id.menu_debug:
        		// TODO: ReminderService/Receiver TEST
        		try {
        			Context context = getApplicationContext();
//    				Toast.makeText(context,  "Feature not available.",  Toast.LENGTH_SHORT).show();
        			
        			// get selected food types
        			Set<String> foodPlate = appPreferences.getStringSet("pref_food_list_key", null);
        			Set<String> mealTimes = appPreferences.getStringSet("pref_meal_list_key", null);
        			Set<String> snackTimes = appPreferences.getStringSet("pref_snack_list_key", null);
        			
        			PhotoManager iPhoto = new PhotoManager();
        			Uri nextPhoto = iPhoto.getPrimingPhoto(foodPlate, mealTimes, snackTimes);
        			
        			if (nextPhoto != null) {
            			/*
    						ImageView imagePreview = (ImageView) findViewById(R.id.preview);
    						        
    						final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
    						final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
    						final Bitmap bitmap = ((BitmapDrawable)wallpaperDrawable).getBitmap();
            			 */
        			}
        			
        			// debug statement
        			Toast.makeText(context, nextPhoto.toString(),  Toast.LENGTH_SHORT).show();
        			
        			
//        			Toast.makeText(context,  iPhoto.pickFood(new String[] {"banana", "apple", "toast", "pickle", "legumes", "meat", "fish", "seafood"})
//        						,  Toast.LENGTH_SHORT).show();
/*
        			// set pendingIntent to a load web page for the daily survey
        			String surveyPath = getResources().getString(R.string.uri_survey);
        	    	Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(surveyPath));
        	    	//PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0
        	    			//, new Intent() // browserIntent
        	    			//, PendingIntent.FLAG_UPDATE_CURRENT);
        			
        			int SURVEY_REMINDER_REQUESTCODE = 1234567;
        			CharSequence app_name = getText(R.string.app_name);
        			CharSequence message = getText(R.string.reminder_survey_alert);
        			Context context = getApplicationContext();
        			
        			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        			
        			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        			Notification notif = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis());
        			notif.setLatestEventInfo(context, app_name, message, contentIntent);
        			nm.notify(1, notif);
        				 //}        			
        				  */
        			/*
        			Intent resultIntent = new Intent(this, MainActivity.class);
        			PendingIntent rPI = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);        			
        	    	// @drawable/ic_action_new_event
        	    	NotificationCompat.Builder sReminder = new NotificationCompat.Builder(this)
        	    												.setSmallIcon(R.drawable.ic_launcher)
//        	    												.addAction(R.drawable.ic_action_new_event, "open", pi)
        	    												.setContentTitle(title)
        	    												.setContentText(message);
        	    	sReminder.setContentIntent(rPI);
        	    	
        	    	NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        	    	notifyMgr.notify(SURVEY_REMINDER_REQUESTCODE, sReminder.build());
        	    	*/        			
        		} catch (Exception ex2) {
        			Log.e(LOG_TAG + ".onOptionsItemSelected", ex2.toString(), ex2);
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
        	// load survey page URL in a browser window
	        case R.id.menu_survey:
	        	// set user expectations
	        	Toast.makeText(this, "Opening Survey...", Toast.LENGTH_SHORT).show();
	        	// get path to survey
	        	String surveyPath = getResources().getString(R.string.uri_survey);

	        	try {
	        		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(surveyPath));
	        		startActivity(browserIntent);
	        	} 
	        	catch (Exception ew) {
	        		// surveyPath
	        		Toast.makeText(this, "Failed to load survey:\r\n" + surveyPath, Toast.LENGTH_LONG).show();
	        		// Toast.makeText(this, ew.toString(), Toast.LENGTH_SHORT).show();
	        	}
	        	/*  // if you want then web browser open in your activity then do like this:
					WebView webView = (WebView) findViewById(R.id.webView1);
					WebSettings settings = webview.getSettings();
					settings.setJavaScriptEnabled(true);
					webView.loadUrl(URL);
					if you want to use zoom control in your browser then you can use:
					
					settings.setSupportZoom(true);
					settings.setBuiltInZoomControls(true);
	        	 */
	        	break;
	        default:
	        	// home & up buttons; as you specify a parent activity in AndroidManifest.xml.
	            return super.onOptionsItemSelected(item);
        }        
        
        return true;
    }
    
    private void RegisterReminderBroadcast() {
    	try {
	    	Intent intent = new Intent(this, ReminderReceiver.class);
	    	
	    	intent.putExtra("alarm_message", getResources().getString(R.string.reminder_survey_alert));
	    	// cancel any existing reminder
	    	PendingIntent pendingIntent = PendingIntent.getBroadcast(this, SURVEY_REMINDER_REQUESTCODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    	
	    	if (appPreferences.getBoolean("pref_reminder_key", true)) {
		    	// TODO: modify to use debug, set for 1 minute delay ... otherwise calculate time based on user preference
		    	Calendar cal = Calendar.getInstance();
		    	// start with system time
		    	cal.setTimeInMillis(System.currentTimeMillis());
		    	// get user specified time for bed (default to 21)
		    	String tempSleep = appPreferences.getString("pref_bodyclock_sleep_key", getResources().getString(R.string.pref_bodyclock_sleep_default));
		    	// convert to int
		    	Integer sleepTime = Integer.valueOf(tempSleep);
		    	// start with sleep time
		    	cal.set(Calendar.HOUR_OF_DAY, sleepTime);		    	
		    	
		    	if (BuildConfig.DEBUG) {
		    		// trigger immediately
		    		cal.add(Calendar.DAY_OF_YEAR, -1);
		    	} else {
		    		// turn clock back by 3 hours
		    		cal.add(Calendar.HOUR_OF_DAY, -3);
		    	}
		    	if (BuildConfig.DEBUG) {
		    		// dump next alarm
		    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    		sdf.setTimeZone(TimeZone.getTimeZone("GMT+5"));
		    		Log.d(LOG_TAG + ".RegisterReminderBroadcast", sdf.format(cal.getTime()));
		    	}
		    	//PendingIntent pendingIntent = PendingIntent.getBroadcast(this, SURVEY_REMINDER_REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		    																							// .INTERVAL_FIFTEEN_MINUTES
		    																							// .INTERVAL_DAY
		    	//amReminder.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
		    	amReminder.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
