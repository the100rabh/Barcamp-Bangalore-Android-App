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
package com.bangalore.barcamp.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class GCMUtils {

	private static final String APP_SETTINGS = "AppSettings";
	private static final String REG_ID = "RegID";
	public static final String SENDER_ID = null;

	public static boolean isRegistered(Context context) {
		boolean retVal = false;
		SharedPreferences sharedPrefs = context.getSharedPreferences(
				APP_SETTINGS, Context.MODE_PRIVATE);
		String val = sharedPrefs.getString(REG_ID, null);
		retVal = !TextUtils.isEmpty(val);
		return retVal;
	}

	public static void setRegistered(Context context, String regId) {
		SharedPreferences settings = context.getSharedPreferences(APP_SETTINGS,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(REG_ID, regId);
		editor.commit();
	}

}
