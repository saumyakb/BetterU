package edu.cornell.info6130.betterU;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver {
	private String LOG_TAG = "ReminderReceiver";
	private static int SURVEY_REMINDER_REQUESTCODE = 1234567; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			String message = bundle.getString("alarm_message");
			String url = bundle.getString("alarm_url");

    		if (BuildConfig.DEBUG){ 
    			Log.d(LOG_TAG + ".onReceive", "Preparing Notification...");
    		}
			
			createNotification(context, intent, message, url);

    		if (BuildConfig.DEBUG){ 
    			Log.d(LOG_TAG + ".onReceive", "...sent");
    		}
			
		} catch (Exception ex) {
			Log.e(LOG_TAG + ".onReceive", ex.toString(), ex);
		}
	}
	
	public void createNotification(Context context, Intent intent, String message, String url) {
		try {
				CharSequence app_name = context.getText(R.string.app_name);

				// prepare action for notification click event (i.e., open browser to specified URL)
				Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
				notificationIntent.setData(Uri.parse(url));
				//.setTicker(res.getString(R.string.your_ticker))
				PendingIntent pending = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
								
				Notification noti = new Notification.Builder(context)
										 .setAutoCancel(true)
										 .setContentIntent(pending)
								         .setContentTitle(app_name)
								         .setContentText(message)
								         .setSmallIcon(R.drawable.ic_launcher)
 								         .setTicker(message)
								         .build();
				
				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			    // using the same tag and Id causes the new notification to replace an existing one
			    nm.notify(SURVEY_REMINDER_REQUESTCODE, noti);

/* deprecated API
				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				// prepare action for notification click event (i.e., open browser to specified URL)
				Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
				notificationIntent.setData(Uri.parse(url));
				// new Intent()
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				Notification notif = new Notification(R.drawable.ic_launcher, message, System.currentTimeMillis());
				notif.setLatestEventInfo(context, app_name, message, contentIntent);
				notif.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
				nm.notify(SURVEY_REMINDER_REQUESTCODE, notif);
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
