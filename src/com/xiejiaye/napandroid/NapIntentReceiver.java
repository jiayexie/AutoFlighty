package com.xiejiaye.napandroid;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class NapIntentReceiver extends BroadcastReceiver {
	
	static final String TURN_ON_AIRPLANE = "com.xiejiaye.napandroid.TURN_ON_AIRPLANE";
	static final String TURN_OFF_AIRPLANE = "com.xiejiaye.napandroid.TURN_OFF_AIRPLANE";

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		Log.d("napandroid", action);
		
		if (TURN_ON_AIRPLANE.equals(action)) {
			Settings.System.putInt(context.getContentResolver(), 
					Settings.System.AIRPLANE_MODE_ON, 1);
			
			// Post an intent to reload
			intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", 1);
			context.sendBroadcast(intent);
			
		} else if (TURN_OFF_AIRPLANE.equals(action)) {
			Settings.System.putInt(context.getContentResolver(), 
					Settings.System.AIRPLANE_MODE_ON, 0);
			
			// Post an intent to reload
			intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", 0);
			context.sendBroadcast(intent);
			
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			Config config = new Config(context);
			Log.d("napandroid", config.toString());
			AlarmManager alarmManager = (AlarmManager) 
					context.getSystemService(Context.ALARM_SERVICE);
			Log.d("napandroid", alarmManager.toString());
			if (config.isTurnOnAirplaneModeEnabled()) {
				config.registerOnAlarm(context, alarmManager);
			}
			if (config.isTurnOffAirplaneModeEnabled()) {
				config.registerOffAlarm(context, alarmManager);
			}
		}
	}

}
