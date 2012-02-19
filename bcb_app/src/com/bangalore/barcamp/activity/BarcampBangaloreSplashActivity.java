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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.service.UpdatesIntentService;

public class BarcampBangaloreSplashActivity extends Activity {

	public Handler generalHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = new Intent(BarcampBangaloreSplashActivity.this,
					HomeActivity.class);
			startActivity(intent);
			finish();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		generalHandler.sendEmptyMessageDelayed(0, 3000);

		Intent updatesIntent = new Intent(this, UpdatesIntentService.class);
		startService(updatesIntent);

	}
}