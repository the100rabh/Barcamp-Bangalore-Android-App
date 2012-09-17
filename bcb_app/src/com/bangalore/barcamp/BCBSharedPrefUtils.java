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

import android.content.Context;
import android.content.SharedPreferences;

public class BCBSharedPrefUtils {

	private static final String ALARM_PREFIX = "BCB11_Alarm_Prefix";
	private static final String BCB11_ALARMS = "BCB11_Alarm";
	private static final String BCB_SHARED_PREF = "BCBSharedPreference";
	private static final String BCB_SHARED_WITH_ITEM = "BCBSharedWithItem";
	private static final String BCB_LAST_UPDATE_TIME = "BCBLastUpdateTime";
	private static final String BCB_UPDATES_SHARED_PREF = "BCBUpdatesSharedPreference";
	private static final String BCB_UPDATES = "BCBUpdates";
	private static final String BCB_UPDATE_NOTIFICATION_STATE = "BCBUpdateNotificationState";

	public static final int ALARM_NOT_SET = 0;
	public static final int ALARM_SET = 1;
	private static final String BCB_UPDATE_AVAILABLE = "BCBUpdateAvailable";

	public static String getShareSettings(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_SHARED_PREF, Context.MODE_PRIVATE);
		return settings.getString(BCB_SHARED_WITH_ITEM, "Twitter");
	}

	public static void setShareSettings(Context context, String label) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_SHARED_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(BCB_SHARED_WITH_ITEM, label);
		editor.commit();
	}

	public static int getAlarmSettingsForID(Context context, String id) {
		SharedPreferences settings = context.getSharedPreferences(BCB11_ALARMS,
				Context.MODE_PRIVATE);
		return settings.getInt(ALARM_PREFIX + id, ALARM_NOT_SET);
	}

	public static void setAlarmSettingsForID(Context context, String id, int val) {
		SharedPreferences settings = context.getSharedPreferences(BCB11_ALARMS,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(ALARM_PREFIX + id, val);
		editor.commit();
	}

	public static String getBCBUpdatesLastTime(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		return settings.getString(BCB_LAST_UPDATE_TIME, "");
	}

	public static void setBCBUpdatesLastTime(Context context, String time) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(BCB_LAST_UPDATE_TIME, time);
		editor.commit();
	}

	public static String getAllBCBUpdates(Context context, String updates) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		return settings.getString(BCB_UPDATES, updates);
	}

	public static void setAllBCBUpdates(Context context, String updates) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(BCB_UPDATES, updates);
		editor.commit();
	}

	public static boolean getUpdatesNotificationEnabled(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		return settings.getBoolean(BCB_UPDATE_NOTIFICATION_STATE, false);
	}

	public static void setUpdatesNotificationEnabled(Context context,
			boolean val) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(BCB_UPDATE_NOTIFICATION_STATE, val);
		editor.commit();
	}

	public static void setScheduleUpdated(Context context, boolean val) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(BCB_UPDATE_AVAILABLE, val);
		editor.commit();
	}

	public static boolean getScheduleUpdated(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				BCB_UPDATES_SHARED_PREF, Context.MODE_PRIVATE);
		return settings.getBoolean(BCB_UPDATE_AVAILABLE, false);
	}

}
