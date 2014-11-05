package edu.cornell.info6130.betterU;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {
	private String LOG_TAG = "ReminderReceiver";
	private static int SURVEY_REMINDER_REQUESTCODE = 1234567; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			String message = bundle.getString("alarm_message");

    		if (BuildConfig.DEBUG){ 
    			Log.d(LOG_TAG + ".onReceive", "Preparing Notification...");
    		}
//			Toast.makeText(context,  message,  Toast.LENGTH_SHORT).show();
			
			createNotification(context, intent);

    		if (BuildConfig.DEBUG){ 
    			Log.d(LOG_TAG + ".onReceive", "...sent");
    		}
			
		} catch (Exception ex) {
			Log.e(LOG_TAG + ".onReceive", ex.toString(), ex);
		}
	}
	
	public void createNotification(Context context, Intent intent) {
		try {
				CharSequence app_name = context.getText(R.string.app_name);
				CharSequence message = context.getText(R.string.reminder_survey_alert);
				
				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
				Notification notif = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis());
				notif.setLatestEventInfo(context, app_name, message, contentIntent);
				nm.notify(SURVEY_REMINDER_REQUESTCODE, notif);
			
/*			
			CharSequence app_name = context.getText(R.string.app_name);
			CharSequence message = context.getText(R.string.reminder_survey_alert);
	
			// set pendingIntent to a load web page for the daily survey
			String surveyPath = context.getResources().getString(R.string.uri_survey);
	    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(surveyPath));
	    	PendingIntent pi = PendingIntent.getActivity(context, 0, browserIntent, 0);
			
	    	// @drawable/ic_action_new_event
	    	NotificationCompat.Builder sReminder = new NotificationCompat.Builder(context)
	    												.setSmallIcon(R.drawable.ic_launcher)
	    												.addAction(R.drawable.ic_action_new_event, "open", pi)
	    												.setContentTitle(app_name)
	    												.setContentText(message);
	    	
	    	Intent resultIntent = new Intent(context, MainActivity.class);
	    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
	    	stackBuilder.addParentStack(MainActivity.class);
	    	stackBuilder.addNextIntent(resultIntent);
	    	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	    	sReminder.setContentIntent(resultPendingIntent);
	    	NotificationManager appNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    	appNotifyManager.notify(SURVEY_REMINDER_REQUESTCODE, sReminder.build());
*/	    	
		} catch (Exception ex) {
			Log.e(LOG_TAG + ".createNotification", ex.toString(), ex);
		}
    	/*
		// build notification
		Notification reminder = new Notification.Builder(context)
													.setSmallIcon(R.drawable.ic_launcher)
													.setContentTitle(ttl)
													.setContentText(msg)
//													.setContentIntent(pi)
													.build();
		reminder.notify();
		*/
	  }
}
