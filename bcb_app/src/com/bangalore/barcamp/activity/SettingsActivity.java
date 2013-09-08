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

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;

public class SettingsActivity extends BCBActivityBaseClass {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_screen);

		BCBUtils.createActionBarOnActivity(this);
		BCBUtils.addNavigationActions(this);
		((CheckBox) findViewById(R.id.notificationSettingsCheckBox))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// BCBSharedPrefUtils.setUpdatesNotificationEnabled(
						// getApplicationContext(), isChecked);
						// Intent newIntent = new
						// Intent(getApplicationContext(),
						// UpdatesIntentService.class);
						// PendingIntent pendingIntent =
						// PendingIntent.getService(
						// getApplicationContext(), 1000, newIntent,
						// PendingIntent.FLAG_ONE_SHOT);
						// AlarmManager alarmManager = (AlarmManager)
						// getSystemService(Context.ALARM_SERVICE);
						// if (isChecked) {
						// long timeInMills = System.currentTimeMillis() +
						// 30000;// 420000;
						// alarmManager.set(AlarmManager.RTC_WAKEUP,
						// timeInMills, pendingIntent);
						//
						// } else {
						// alarmManager.cancel(pendingIntent);
						// }
					}
				});
	}
}
