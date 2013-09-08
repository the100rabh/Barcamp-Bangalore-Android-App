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

import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.SessionAttendingUpdateService;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;
import com.bangalore.barcamp.widgets.CircularImageView;
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
		try {
			((CircularImageView) findViewById(R.id.authorImage))
					.setImageURL(new URL(
							"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
		checkBox.setChecked(BCBSharedPrefUtils.getAlarmSettingsForID(this,
				session.id) == BCBSharedPrefUtils.ALARM_SET);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					BCBUtils.removeSessionFromSchedule(getApplicationContext(),
							slot, session,
							getIntent().getIntExtra(EXTRA_SLOT_POS, 0),
							getIntent().getIntExtra(EXTRA_SESSION_POSITION, 0));
				} else {
					BCBUtils.setAlarmForSession(getApplicationContext(),
							session.id,
							getIntent().getIntExtra(EXTRA_SLOT_POS, 0),
							getIntent().getIntExtra(EXTRA_SESSION_POSITION, 0));
				}
				Intent newIntent = new Intent(getApplicationContext(),
						SessionAttendingUpdateService.class);
				newIntent.putExtra(SessionAttendingUpdateService.SESSION_ID,
						session.id);
				newIntent.putExtra(SessionAttendingUpdateService.IS_ATTENDING,
						isChecked ? "true" : "false");
				startService(newIntent);
			}
		});

		findViewById(R.id.location_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(),
								LocationActivity.class);
						intent.putExtra(LocationActivity.LOCATION_EXTRA,
								session.location);
						startActivity(intent);

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
