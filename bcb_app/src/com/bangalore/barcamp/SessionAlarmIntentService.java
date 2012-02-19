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

package com.bangalore.barcamp;

import java.io.BufferedReader;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bangalore.barcamp.activity.SessionDetailsActivity;
import com.bangalore.barcamp.data.BarcampBangalore;

public class SessionAlarmIntentService extends IntentService {

	public static final String SESSION_ID = "SessionID";
	public final static String EXTRA_SESSION_POSITION = "session_position";
	public final static String EXTRA_SLOT_POS = "slotPosition";

	public SessionAlarmIntentService() {
		super("SessionAlarmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("SessionAlarmIntentService", " Service called ");
		boolean retVal = false;
		BufferedReader in = null;
		Context context = getApplicationContext(); // application Context
		if (((BarcampBangalore) context).getBarcampData() == null) {
			retVal = BCBUtils
					.updateContextWithBarcampData(getApplicationContext());
		} else {
			retVal = true;
		}
		if (retVal) {
			int icon = R.drawable.app_logo; // icon from resources
			CharSequence tickerText = "Hello"; // ticker-text
			long when = System.currentTimeMillis(); // notification time
			CharSequence contentTitle = "My notification"; // message title
			CharSequence contentText = "Hello World!"; // message text

			Intent notificationIntent = new Intent(this,
					SessionDetailsActivity.class);
			notificationIntent.putExtra(
					SessionDetailsActivity.EXTRA_SESSION_POSITION,
					intent.getIntExtra(EXTRA_SESSION_POSITION, 0));
			notificationIntent.putExtra(SessionDetailsActivity.EXTRA_SLOT_POS,
					intent.getIntExtra(EXTRA_SLOT_POS, 0));
			notificationIntent.putExtra(
					SessionDetailsActivity.EXTRA_SESSION_ID,
					intent.getStringExtra(SESSION_ID));
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);

			// the next two lines initialize the Notification, using the
			// configurations above
			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.DEFAULT_LIGHTS
					| Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE
					| Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			mNotificationManager.notify(100, notification);
		}
	}
}
