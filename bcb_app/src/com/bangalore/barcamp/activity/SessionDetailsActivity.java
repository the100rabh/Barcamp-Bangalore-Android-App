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

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class SessionDetailsActivity extends BCBActivityBaseClass {
	public final static String EXTRA_SESSION_POSITION = "session_position";
	public final static String EXTRA_SLOT_POS = "slotPosition";
	public static final String EXTRA_SESSION_ID = "sessionID";
	private static final int SHOW_ERROR_DIALOG = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_details);

		BCBUtils.createActionBarOnActivity(this);
		BCBUtils.addNavigationActions(this);
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
		((TextView) findViewById(R.id.presenter)).setText("By "
				+ session.presenter);
		((TextView) findViewById(R.id.description)).setText(Html
				.fromHtml(session.description));
		((TextView) findViewById(R.id.description))
				.setMovementMethod(LinkMovementMethod.getInstance());

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
					Log.e("Session", "hour : " + hour + " mins :" + mins);
					GregorianCalendar date = new GregorianCalendar(2012,
							Calendar.AUGUST, 25, hour, mins);
					long timeInMills = date.getTimeInMillis() - 300000;
					alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills,
							intent);
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

		Intent intent = new Intent(this, ShareActivity.class);
		intent.putExtra(ShareActivity.SHARE_STRING, "I am attending session "
				+ session.title + " by " + session.presenter + " @"
				+ session.location + " between " + session.time);
		IntentAction shareAction = new IntentAction(this, intent,
				R.drawable.share_icon);

		ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		actionbar.addAction(shareAction);

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
							ScheduleActivity.class);
					startActivity(intent);
				}
			});
			break;
		}
		return alertDialog;
	}

}
