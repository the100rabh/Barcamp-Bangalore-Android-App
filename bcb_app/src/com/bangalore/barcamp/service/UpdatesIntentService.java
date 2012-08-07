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

package com.bangalore.barcamp.service;

import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.activity.ScheduleActivity;

public class UpdatesIntentService extends IntentService {

	public UpdatesIntentService() {
		super("UpdatesIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!BCBSharedPrefUtils
				.getUpdatesNotificationEnabled(getApplicationContext())) {
			return;
		}
		RSSReader reader = new RSSReader();
		String uri = "https://api.twitter.com/1/statuses/user_timeline.rss?screen_name=bcbupdates";
		try {
			RSSFeed feed = reader.load(uri);
			List<RSSItem> items = feed.getItems();
			String lastUpdateDate = BCBSharedPrefUtils
					.getBCBUpdatesLastTime(getApplicationContext());
			int count = 0;
			for (RSSItem item : items) {
				count++;
				if (count == 1) {
					BCBSharedPrefUtils.setBCBUpdatesLastTime(
							getApplicationContext(), item.getPubDate()
									.toGMTString());
				} else if (count == 3) {
					break;
				}

				if (item.getPubDate().toGMTString().equals(lastUpdateDate)) {
					break;
				}

				int icon = R.drawable.bcb_logo; // icon from resources
				CharSequence tickerText = "Barcamp Bangalore Updates"; // ticker-text
				long when = 0;// item.getPubDate().getTime(); // notification
								// time
				CharSequence contentTitle = "Barcamp Bangalore"; // message
																	// title
				CharSequence contentText = item.getTitle(); // message text

				Intent notificationIntent = new Intent(getApplicationContext(),
						ScheduleActivity.class);

				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent notificationPendingIntent = PendingIntent
						.getActivity(this, 0, notificationIntent, 0);

				Notification notification = new Notification(icon, tickerText,
						when);
				notification.flags |= Notification.DEFAULT_LIGHTS
						| Notification.DEFAULT_SOUND
						| Notification.DEFAULT_VIBRATE
						| Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo(getApplicationContext(),
						contentTitle, contentText, notificationPendingIntent);

				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
				mNotificationManager.notify(item.getPubDate().hashCode(),
						notification);

			}

		} catch (RSSReaderException e) {
			Log.e("UpdatesIntentService", e.getMessage());
		} catch (Exception e) {
			Log.e("UpdatesIntentService", e.getMessage());
		}

		if (BCBSharedPrefUtils
				.getUpdatesNotificationEnabled(getApplicationContext())) {
			Intent newIntent = new Intent(getApplicationContext(),
					UpdatesIntentService.class);
			PendingIntent pendingIntent = PendingIntent.getService(
					getApplicationContext(), 1000, newIntent,
					PendingIntent.FLAG_ONE_SHOT);
			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			long timeInMills = System.currentTimeMillis() + 420000;
			alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills,
					pendingIntent);

		}
	}
}
