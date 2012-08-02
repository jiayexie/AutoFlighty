package com.xiejiaye.napandroid;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ConfigureActivity extends ListActivity {
	
	ConfigSharedPreferences mPrefs;
	AlarmManager mAlarmManager;
	
	static final long DAY = 24*60*60*1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPrefs = new ConfigSharedPreferences(this);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		getListView().setAdapter(new ConfigureListAdapter());
		getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch (position) {
				case 1:
					Time onTime = mPrefs.getTurnOnTime();
					new TimePickerDialog(ConfigureActivity.this, 
							TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, 
							new TimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							cancelOnAlarm();
							mPrefs.setTurnOnHourMinute(hourOfDay, minute);
							if (mPrefs.isTurnOnAirplaneModeEnabled()) registerOnAlarm();
							((BaseAdapter) getListView().getAdapter()).notifyDataSetChanged();
						}
					}, onTime.hour, onTime.minute, true).show();
					break;
				case 3:
					Time offTime = mPrefs.getTurnOffTime();
					new TimePickerDialog(ConfigureActivity.this, 
							TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, 
							new TimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							cancelOffAlarm();
							mPrefs.setTurnOffHourMinute(hourOfDay, minute);
							if (mPrefs.isTurnOffAirplaneModeEnabled()) registerOffAlarm();
							((BaseAdapter) getListView().getAdapter()).notifyDataSetChanged();
						}
					}, offTime.hour, offTime.minute, true).show();
					break;
				}
			}
		});
	}
	
	private class ConfigureListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
			TextView tv;
			switch (position) {
			case 0:
				v = inflater.inflate(R.layout.item_checkbox, null);
				tv = (CompoundButton) v.findViewById(R.id.checkbox);
				((CompoundButton) tv).setChecked(mPrefs.isTurnOnAirplaneModeEnabled());
				tv.setText(R.string.turn_on_airplane_enabled);
				((CompoundButton) tv).setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						mPrefs.setTurnOnAirplaneModeEnabled(isChecked);

						if (isChecked) registerOnAlarm();
						else cancelOnAlarm();
					}
				});
				break;
			case 1:
				v = inflater.inflate(R.layout.item_textview, null);
				tv = (TextView) v.findViewById(R.id.textview);
				tv.setText(mPrefs.getTurnOnTime().format("%H:%M"));
				break;
			case 2:
				v = inflater.inflate(R.layout.item_checkbox, null);
				tv = (CompoundButton) v.findViewById(R.id.checkbox);
				((CompoundButton) tv).setChecked(mPrefs.isTurnOffAirplaneModeEnabled());
				tv.setText(R.string.turn_off_airplane_enabled);
				((CompoundButton) tv).setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						mPrefs.setTurnOffAirplaneModeEnabled(isChecked);
						
						if (isChecked) registerOffAlarm();
						else cancelOffAlarm();
					}
				});
				break;
			case 3:
				v = inflater.inflate(R.layout.item_textview, null);
				tv = (TextView) v.findViewById(R.id.textview);
				tv.setText(mPrefs.getTurnOffTime().format("%H:%M"));
				break;
			}
			return v;
		}
	}
	
	void registerOnAlarm() {

		Intent intent = new Intent(NapIntentReceiver.TURN_ON_AIRPLANE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);

		mAlarmManager.setRepeating(AlarmManager.RTC, 
				mPrefs.getNextTurnOnTime(), DAY, pendingIntent);
	}
	
	void cancelOnAlarm() {

		Intent intent = new Intent(NapIntentReceiver.TURN_ON_AIRPLANE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);
		
		mAlarmManager.cancel(pendingIntent);
	}
	
	void registerOffAlarm() {
		
		Intent intent = new Intent(NapIntentReceiver.TURN_OFF_AIRPLANE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);

		mAlarmManager.setRepeating(AlarmManager.RTC, 
				mPrefs.getNextTurnOffTime(), DAY, pendingIntent);
	}
	
	void cancelOffAlarm() {

		Intent intent = new Intent(NapIntentReceiver.TURN_OFF_AIRPLANE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);
		
		mAlarmManager.cancel(pendingIntent);
	}
	
	private class ConfigSharedPreferences {
		
		private static final String KEY_ON_ENABLED = "turn_on_enabled";
		private static final String KEY_OFF_ENABLED = "turn_off_enabled";
		private static final String KEY_ON_HOUR = "turn_on_hour";
		private static final String KEY_ON_MINUTE = "turn_on_minute";
		private static final String KEY_OFF_HOUR = "turn_off_hour";
		private static final String KEY_OFF_MINUTE = "turn_off_minute";
		
		private SharedPreferences mPrefs;
		private Editor mEditor;
		
		public ConfigSharedPreferences(Context context) {
			mPrefs = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			mEditor = mPrefs.edit();
		}
		
		boolean isTurnOnAirplaneModeEnabled() {
			return mPrefs.getBoolean(KEY_ON_ENABLED, false);
		}
		boolean isTurnOffAirplaneModeEnabled() {
			return mPrefs.getBoolean(KEY_OFF_ENABLED, false);
		}
		Time getTurnOnTime() {
			Time time = new Time();
			time.set(0, mPrefs.getInt(KEY_ON_MINUTE, 0), mPrefs.getInt(KEY_ON_HOUR, 0),
					0, 0, 0);
			return time;
		}
		Time getTurnOffTime() {
			Time time = new Time();
			time.set(0, mPrefs.getInt(KEY_OFF_MINUTE, 0), mPrefs.getInt(KEY_OFF_HOUR, 0),
					0, 0, 0);
			return time;
		}
		long getNextTurnOnTime() {
			Time time = new Time();
			time.setToNow();
			time.minute = mPrefs.getInt(KEY_ON_MINUTE, 0);
			time.hour = mPrefs.getInt(KEY_ON_HOUR, 0);
			long ans = time.normalize(true);
			return ans > System.currentTimeMillis() ? ans : ans + DAY;
		}
		long getNextTurnOffTime() {
			Time time = new Time();
			time.setToNow();
			time.minute = mPrefs.getInt(KEY_OFF_MINUTE, 0);
			time.hour = mPrefs.getInt(KEY_OFF_HOUR, 7);
			long ans = time.normalize(true);
			return ans > System.currentTimeMillis() ? ans : ans + DAY;
		}
		
		void setTurnOnAirplaneModeEnabled(boolean onEnabled) {
			mEditor.putBoolean(KEY_ON_ENABLED, onEnabled).commit();
		}
		void setTurnOffAirplaneModeEnabled(boolean offEnabled) {
			mEditor.putBoolean(KEY_OFF_ENABLED, offEnabled).commit();
		}
		void setTurnOnHourMinute(int hour, int minute) {
			mEditor.putInt(KEY_ON_HOUR, hour).putInt(KEY_ON_MINUTE, minute).commit();
		}
		void setTurnOffHourMinute(int hour, int minute) {
			mEditor.putInt(KEY_OFF_HOUR, hour).putInt(KEY_OFF_MINUTE, minute).commit();
		}
	}
}
