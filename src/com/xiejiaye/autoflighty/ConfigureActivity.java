package com.xiejiaye.autoflighty;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.TimePickerDialog;
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

/**
 * Simple Activity for configuring whether and when to turn on/off airplane mode.
 * @author Xie Jiaye
 */
public class ConfigureActivity extends ListActivity {
	
	private Config mConfig;
	private AlarmManager mAlarmManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mConfig = new Config(this);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		getListView().setAdapter(new ConfigureListAdapter());
		getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("InlinedApi")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch (position) {
				case 1:
					Time onTime = mConfig.getTurnOnTime();
					new TimePickerDialog(ConfigureActivity.this, 
							TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, 
							new TimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							mConfig.cancelOnAlarm(getApplicationContext(), mAlarmManager);
							mConfig.setTurnOnHourMinute(hourOfDay, minute);
							if (mConfig.isTurnOnAirplaneModeEnabled()) {
								mConfig.registerOnAlarm(getApplicationContext(), mAlarmManager);
							}
							((BaseAdapter) getListView().getAdapter()).notifyDataSetChanged();
						}
					}, onTime.hour, onTime.minute, true).show();
					break;
				case 3:
					Time offTime = mConfig.getTurnOffTime();
					new TimePickerDialog(ConfigureActivity.this, 
							TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, 
							new TimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							mConfig.cancelOffAlarm(getApplicationContext(), mAlarmManager);
							mConfig.setTurnOffHourMinute(hourOfDay, minute);
							if (mConfig.isTurnOffAirplaneModeEnabled()) {
								mConfig.registerOffAlarm(getApplicationContext(), mAlarmManager);
							}
							((BaseAdapter) getListView().getAdapter()).notifyDataSetChanged();
						}
					}, offTime.hour, offTime.minute, true).show();
					break;
				}
			}
		});
	}
	
	/**
	 * ListAdapter for the main ListView.
	 * @author Xie Jiaye
	 */
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
			case 0: // whether auto-on is enabled
				v = inflater.inflate(R.layout.item_checkbox, null);
				tv = (CompoundButton) v.findViewById(R.id.checkbox);
				((CompoundButton) tv).setChecked(mConfig.isTurnOnAirplaneModeEnabled());
				tv.setText(R.string.turn_on_airplane_enabled);
				((CompoundButton) tv).setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						mConfig.setTurnOnAirplaneModeEnabled(isChecked);
						if (isChecked) {
							mConfig.registerOnAlarm(getApplicationContext(), mAlarmManager);
						} else {
							mConfig.cancelOnAlarm(getApplicationContext(), mAlarmManager);
						}
						notifyDataSetChanged();
					}
				});
				break;
			case 1: // if auto-on is enabled, when
				if (mConfig.isTurnOnAirplaneModeEnabled()) {
					v = inflater.inflate(R.layout.item_textview, null);
					tv = (TextView) v.findViewById(R.id.textview);
					tv.setText(getString(R.string.turn_on_time, 
							mConfig.getTurnOnTime().format("%H:%M")));
				} else {
					v = new View(ConfigureActivity.this);
				}
				break;
			case 2: // whether auto-off is enabled
				v = inflater.inflate(R.layout.item_checkbox, null);
				tv = (CompoundButton) v.findViewById(R.id.checkbox);
				((CompoundButton) tv).setChecked(mConfig.isTurnOffAirplaneModeEnabled());
				tv.setText(R.string.turn_off_airplane_enabled);
				((CompoundButton) tv).setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						mConfig.setTurnOffAirplaneModeEnabled(isChecked);
						if (isChecked) {
							mConfig.registerOffAlarm(getApplicationContext(), mAlarmManager);
						} else {
							mConfig.cancelOffAlarm(getApplicationContext(), mAlarmManager);
						}
						notifyDataSetChanged();
					}
				});
				break;
			case 3: // if auto-off is enabled, when
				if (mConfig.isTurnOffAirplaneModeEnabled()) {
					v = inflater.inflate(R.layout.item_textview, null);
					tv = (TextView) v.findViewById(R.id.textview);
					tv.setText(getString(R.string.turn_off_time,
							mConfig.getTurnOffTime().format("%H:%M")));
				} else {
					v = new View(ConfigureActivity.this);
				}
				break;
			}
			return v;
		}
	}
	
}
