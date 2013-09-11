package com.bangalore.barcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		syncUpdatedSessionsData(context);
	}

	public static void syncUpdatedSessionsData(Context context) {
		String userID = BCBSharedPrefUtils.getUserID(context);
		String userKey = BCBSharedPrefUtils.getUserKey(context);
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		boolean isConnected = activeNetInfo != null
				&& activeNetInfo.isConnected();
		if (isConnected) {
			String dataNotSent = BCBSharedPrefUtils
					.getAndClearDataNotSent(context);
			if (!TextUtils.isEmpty(dataNotSent)) {
				String[] dataArray = dataNotSent.split("/");
				for (String data : dataArray) {
					String[] sessionData = data.split(",");
					BufferedReader in = null;
					String url = String.format(
							SessionAttendingUpdateService.BASE_URL, userID,
							userKey, sessionData[0], sessionData[1]);
					try {
						HttpClient client = new DefaultHttpClient();
						HttpUriRequest request = new HttpGet(url);
						HttpResponse response = client.execute(request);
						in = new BufferedReader(new InputStreamReader(response
								.getEntity().getContent()));
						StringBuffer sb = new StringBuffer("");
						String line = "";
						String NL = System.getProperty("line.separator");
						while ((line = in.readLine()) != null) {
							sb.append(line + NL);
						}
						in.close();
						String page = sb.toString();
						Log.e("SessionAttendingUpdateService", "id:"
								+ sessionData[0] + "  return:" + page);
						if (!page.equals("success\n")) {
							BCBSharedPrefUtils.setDataNotSent(context,
									sessionData[0], sessionData[1]);
						}

					} catch (Throwable e) {
						e.printStackTrace();
						BCBSharedPrefUtils.setDataNotSent(context,
								sessionData[0], sessionData[1]);
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

				}
			}
		}
	}

}
