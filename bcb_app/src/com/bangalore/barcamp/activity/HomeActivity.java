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

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;

public class HomeActivity extends BCBActivityBaseClass {

	protected static final int START_SCHEDULE = 100;
	protected static final int ABOUT_ACTIVITY_REQUEST = 101;
	protected static final int BCB_TWITTER_UPDATES_ACTIVITY_REQUEST = 102;
	public Handler generalHandler = new Handler() {
		public void handleMessage(Message msg) {

		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);

		BCBUtils.addNavigationActions(this);

		BCBUtils.createActionBarOnActivity(this, true);

		((LinearLayout) findViewById(R.id.SessionButtonLayout))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(HomeActivity.this,
								ScheduleActivity.class);
						startActivityForResult(intent, START_SCHEDULE);
					}
				});

		((LinearLayout) findViewById(R.id.tweetsButtonLayout))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(HomeActivity.this,
								WebViewActivity.class);
						intent.putExtra(WebViewActivity.URL,
								"file:///android_asset/bcb11_updates.html");
						startActivityForResult(intent,
								BCB_TWITTER_UPDATES_ACTIVITY_REQUEST);
					}
				});

		((LinearLayout) findViewById(R.id.AboutButtonLayout))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(HomeActivity.this,
								AboutActivity.class);
						intent.putExtra(WebViewActivity.URL,
								"http://barcampbangalore.org");
						startActivityForResult(intent, ABOUT_ACTIVITY_REQUEST);
					}
				});
		((LinearLayout) findViewById(R.id.BCBTwitterUpdatesButtonLayout))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(HomeActivity.this,
								WebViewActivity.class);
						intent.putExtra(WebViewActivity.URL,
								"file:///android_asset/barcamp_updates.html");
						startActivityForResult(intent,
								BCB_TWITTER_UPDATES_ACTIVITY_REQUEST);
					}
				});

		((LinearLayout) findViewById(R.id.MapsButtonLayout))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final PackageManager pm = getPackageManager();

						Intent intent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://maps.google.co.in/maps?q=SAP+Labs+India+Pvt.+Ltd.+-+Bangalore&num=1&t=h&vpsrc=6&ie=UTF8&cid=11444560640179826527&ll=12.978192,77.715204&spn=0.013591,0.022595&z=16&iwloc=A"));
						final List<ResolveInfo> matches = pm
								.queryIntentActivities(intent, 0);
						for (ResolveInfo info : matches) {
							Log.e("MapPackage", info.loadLabel(pm) + " "
									+ info.activityInfo.packageName + " "
									+ info.activityInfo.name);
							if (info.activityInfo.name
									.equals("com.google.android.maps.MapsActivity")) {
								intent.setClassName(
										"com.google.android.apps.maps",
										"com.google.android.maps.MapsActivity");
							}
						}

						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
	}

}
