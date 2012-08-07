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
