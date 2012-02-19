/*
 * Copyright (C) 2012 Saurabh Minni <http://100rabh.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bangalore.barcamp.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bangalore.barcamp.BCBConsts;
import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;

public class SessionDetailsActivity extends Activity {
	public final static String EXTRA_SESSION_POSITION = "session_position";
	public final static String EXTRA_SLOT_POS = "slotPosition";
	public static final String EXTRA_SESSION_ID = "sessionID";
	private static final int SHOW_ERROR_DIALOG = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.session_details);

		BCBUtils.createActionBarOnActivity(this);
		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();
		if (data == null) {
			finish();
			return;
		}
		final Slot slot = data.slotsArray.get(getIntent().getIntExtra(
				EXTRA_SLOT_POS, 0));
		final Session session = slot.sessionsArray.get(getIntent().getIntExtra(
				EXTRA_SESSION_POSITION, 0));
		String id = getIntent().getStringExtra(EXTRA_SESSION_ID);
		if (id != null && !id.equals(session.id)) {
			showDialog(SHOW_ERROR_DIALOG);
			finish();
		}

		((TextView) findViewById(R.id.title)).setText(session.title);
		((TextView) findViewById(R.id.time)).setText(session.time);
		((TextView) findViewById(R.id.location)).setText(session.location);
		((TextView) findViewById(R.id.presenter)).setText(session.presenter);

		CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
		checkBox.setChecked(BCBSharedPrefUtils.getAlarmSettingsForID(this,
				session.id) == BCBSharedPrefUtils.ALARM_SET);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					BCBSharedPrefUtils.setAlarmSettingsForID(
							SessionDetailsActivity.this, session.id,
							BCBSharedPrefUtils.ALARM_SET);
					PendingIntent intent = BCBUtils.createPendingIntentForID(
							SessionDetailsActivity.this, session.id,
							getIntent().getIntExtra(EXTRA_SLOT_POS, 0),
							getIntent().getIntExtra(EXTRA_SESSION_POSITION, 0));
					AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					int hour = slot.startTime / 100;
					int mins = slot.startTime % 100;
					GregorianCalendar date = new GregorianCalendar(2012,
							Calendar.FEBRUARY, 11, hour, mins);
					// date = (GregorianCalendar)
					// GregorianCalendar.getInstance();
					TimeZone tm = TimeZone.getDefault();
					long time = tm.getOffset(date.getTimeInMillis());
					long timeInMills = date.getTimeInMillis() - 10 * 60 * 1000;// +
																				// time;
					long currentTime = System.currentTimeMillis();
					Log.e("TimeData",
							"currentTime: " + currentTime + " AlarmSet for "
									+ timeInMills + " offset: " + time
									+ " date.getTimeInMillis: "
									+ date.getTimeInMillis() + " date: "
									+ date.toString());
					alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills,
							intent);
					alarmManager.set(AlarmManager.RTC_WAKEUP,
							currentTime + 10000, intent);

				} else {
					BCBSharedPrefUtils.setAlarmSettingsForID(
							SessionDetailsActivity.this, session.id,
							BCBSharedPrefUtils.ALARM_NOT_SET);
					PendingIntent intent = BCBUtils.createPendingIntentForID(
							SessionDetailsActivity.this, session.id,
							getIntent().getIntExtra(EXTRA_SLOT_POS, 0),
							getIntent().getIntExtra(EXTRA_SESSION_POSITION, 0));
					AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					alarmManager.cancel(intent);
				}
			}
		});

	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		switch (id) {
		case SHOW_ERROR_DIALOG:
			alertDialog.setCancelable(false);
			alertDialog.setTitle("Error!!!");
			alertDialog
					.setMessage("Error!!! Session got changed. Please check schedule again.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(SHOW_ERROR_DIALOG);
					SessionDetailsActivity.this.finish();
					Intent intent = new Intent(SessionDetailsActivity.this,
							HomeActivity.class);
					startActivity(intent);
				}
			});
			break;
		}
		return alertDialog;
	}
}
