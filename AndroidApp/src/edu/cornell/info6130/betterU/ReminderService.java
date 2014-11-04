package edu.cornell.info6130.betterU;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends Service {
	private NotificationManager _notifyManager;
	// unique ID for the notification
	private int NOTIFICATION = R.string.pref_app_reminder_title;
	
	public class LocalBinder extends Binder {
		ReminderService getService() {
			return ReminderService.this;
		}
	}

	
	@Override
	public void onCreate() {
		_notifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		showNotification();
		// stub
		// super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ReminderService", "Recieved start id " + startId + ": " + intent);
		// run until it is explicitly stopped
		return START_STICKY;
	}
		
	@Override
	public void onDestroy() {
		_notifyManager.cancel(NOTIFICATION);
		
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// stub
		return _Binder;
	}
	
	// this object receives interactions from clients
	private final IBinder _Binder = new LocalBinder();
	
	private void showNotification() {
		CharSequence msg = getText(R.string.pref_app_reminder_desc);
		CharSequence ttl = getText(R.string.app_name);
		
		Intent notifyIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// build notification
		Notification notifyMsg = new Notification.Builder(this.getApplicationContext())
													.setContentTitle(ttl)
													.setContentText(msg)
													.setSmallIcon(R.drawable.ic_launcher)
													.setContentIntent(contentIntent)
													.build();
		
		// send
		_notifyManager.notify(NOTIFICATION, notifyMsg);
	}
}
