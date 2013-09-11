package com.bangalore.barcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SessionAttendingUpdateService extends IntentService {

	public static final String SESSION_ID = "SESSION_ID";
	public static final String IS_ATTENDING = "IS_ATTENDING";
	public static final String BASE_URL = "http://barcampbangalore.org/bcb/wp-android_helper.php?action=setuserdata&userid=%s&userkey=%s&sessionid=%s&isattending=%s";

	public SessionAttendingUpdateService() {
		super("SessionAttendingUpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String userID = BCBSharedPrefUtils.getUserID(getApplicationContext());
		String userKey = BCBSharedPrefUtils.getUserKey(getApplicationContext());
		String sessionID = intent.getStringExtra(SESSION_ID);
		String isAttending = intent.getStringExtra(IS_ATTENDING);

		BufferedReader in = null;
		String url = String.format(BASE_URL, userID, userKey, sessionID,
				isAttending);
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
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
				Log.e("SessionAttendingUpdateService", "id:" + sessionID
						+ "  return:" + page);
				if (!page.equals("success\n")) {
					BCBSharedPrefUtils.setDataNotSent(getApplicationContext(),
							sessionID, isAttending);
				}

			} catch (Throwable e) {
				e.printStackTrace();
				BCBSharedPrefUtils.setDataNotSent(getApplicationContext(),
						sessionID, isAttending);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			BCBSharedPrefUtils.setDataNotSent(getApplicationContext(),
					sessionID, isAttending);
		}

	}

}
