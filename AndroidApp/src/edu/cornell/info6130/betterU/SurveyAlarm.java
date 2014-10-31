package edu.cornell.info6130.betterU;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;

public class SurveyAlarm extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			String message = bundle.getString("alarm_message");			
			Toast.makeText(context,  message,  Toast.LENGTH_SHORT).show();			
		} catch (Exception e) {
			Toast.makeText(context, "Error AlarmReceiver.onReceive" + e.toString(), Toast.LENGTH_SHORT).show();
		}
	}
}
