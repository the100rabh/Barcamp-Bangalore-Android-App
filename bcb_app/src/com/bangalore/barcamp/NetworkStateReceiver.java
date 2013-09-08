package com.bangalore.barcamp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isConnected = activeNetInfo != null
				&& activeNetInfo.isConnectedOrConnecting();
		if (isConnected) {
			String dataNotSent = BCBSharedPrefUtils.getDataNotSent(context);
			if (!TextUtils.isEmpty(dataNotSent)) {
				BCBSharedPrefUtils.setDataNotSent(context, "");
				String[] dataArray = dataNotSent.split("/");
				for (String data : dataArray) {
					String[] sessionData = data.split(",");
					Intent newIntent = new Intent(context,
							SessionAttendingUpdateService.class);
					newIntent.putExtra(
							SessionAttendingUpdateService.SESSION_ID,
							sessionData[0]);
					newIntent.putExtra(
							SessionAttendingUpdateService.IS_ATTENDING,
							sessionData[1]);
					context.startService(newIntent);
				}
			}
		}
	}

}
