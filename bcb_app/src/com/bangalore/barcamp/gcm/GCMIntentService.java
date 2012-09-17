/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bangalore.barcamp.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.activity.ScheduleActivity;
import com.bangalore.barcamp.activity.UpdateMessagesActivity;
import com.bangalore.barcamp.database.MessagesDataSource;

public class GCMIntentService extends IntentService {

	public GCMIntentService() {
		super("GCMIntentService");
	}

	private static PowerManager.WakeLock sWakeLock;
	private static final Object LOCK = GCMIntentService.class;
	private static final String TAG = "GCMIntentService";

	static void runIntentInService(Context context, Intent intent) {
		synchronized (LOCK) {
			if (sWakeLock == null) {
				PowerManager pm = (PowerManager) context
						.getSystemService(Context.POWER_SERVICE);
				sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						"my_wakelock");
			}
		}
		sWakeLock.acquire();
		intent.setClassName(context, GCMIntentService.class.getName());
		context.startService(intent);
	}

	@Override
	public final void onHandleIntent(Intent intent) {
		try {
			String action = intent.getAction();
			if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
				handleRegistration(intent);
			} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
				handleMessage(intent);
			}
		} finally {
			synchronized (LOCK) {
				sWakeLock.release();
			}
		}
	}

	private void handleMessage(Intent intent) {
		int icon = R.drawable.bcb_logo; // icon from resources
		CharSequence tickerText = "Session Alert"; // ticker-text
		long when = System.currentTimeMillis(); // notification time
		CharSequence contentTitle = "My notification"; // message title
		CharSequence contentText = "Hello World!"; // message text

		Log.e(TAG, "Message recieved");
		Log.e(TAG, " Message Type : " + intent.getStringExtra("messageType"));
		Log.e(TAG, " Message " + intent.getStringExtra("message"));

		String messageType = intent.getStringExtra("messageType");

		if (messageType.equals("SCHEDULE_UPDATE")) {
			contentText = intent.getStringExtra("message");
			contentTitle = "Schedule Updated";
			tickerText = "Schedule Updated";
			Intent notificationIntent = new Intent(this, ScheduleActivity.class);
			BCBSharedPrefUtils.setScheduleUpdated(this, true);
			notificationIntent.putExtra(ScheduleActivity.FROM_NOTIFICATION,
					true);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 100,
					notificationIntent, 0);
			// the next two lines initialize the Notification, using the
			// configurations above
			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_AUTO_CANCEL
					| Notification.DEFAULT_SOUND
					| Notification.FLAG_SHOW_LIGHTS;
			notification.setLatestEventInfo(this, contentTitle, contentText,
					contentIntent);
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			mNotificationManager.notify(100, notification);

		} else {
			MessagesDataSource ds = new MessagesDataSource(
					getApplicationContext());
			ds.open();
			String message = intent.getStringExtra("message");
			String timestamp = String.valueOf(when);
			ds.createMessage(message, timestamp);
			ds.close();

			contentText = message;
			contentTitle = "BCB Update";
			tickerText = "Message from BCB";
			Intent notificationIntent = new Intent(this,
					UpdateMessagesActivity.class);
			notificationIntent.putExtra(
					UpdateMessagesActivity.FROM_NOTIFICATION, true);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 90,
					notificationIntent, 0);
			// the next two lines initialize the Notification, using the
			// configurations above
			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_AUTO_CANCEL
					| Notification.DEFAULT_SOUND
					| Notification.FLAG_SHOW_LIGHTS;
			notification.setLatestEventInfo(this, contentTitle, contentText,
					contentIntent);
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			mNotificationManager.notify(90, notification);

		}
		try {
			Uri notificationurl = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notificationurl);
			r.play();
		} catch (Exception e) {
		}
	}

	private void handleRegistration(Intent intent) {
		String registrationId = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error");
		String unregistered = intent.getStringExtra("unregistered");
		// registration succeeded
		if (registrationId != null) {
			// store registration ID on shared preferences
			// notify 3rd-party server about the registered ID
			Log.i(TAG, "Registeration sucess");
			ServerUtilities.register(getApplicationContext(), registrationId);
		}

		// unregistration succeeded
		if (unregistered != null) {
			// get old registration ID from shared preferences
			// notify 3rd-party server about the unregistered ID
		}

		// last operation (registration or unregistration) returned an error;
		if (error != null) {
			if ("SERVICE_NOT_AVAILABLE".equals(error)) {
				// optionally retry using exponential back-off
				// (see Advanced Topics)
			} else {
				// Unrecoverable error, log it
				Log.i(TAG, "Received error: " + error);
			}
		}
	}
}
