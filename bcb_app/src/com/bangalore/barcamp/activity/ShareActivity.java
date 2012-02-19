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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bangalore.barcamp.BCBConsts;
import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.markupartist.android.widget.ActionBar;

public class ShareActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_screen);
		BCBUtils.createActionBarOnActivity(this);
		ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		actionbar.removeActionAt(0);
		((EditText) findViewById(R.id.editText1))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
						((TextView) findViewById(R.id.charsLeftTextView))
								.setText("Chars left: "
										+ (140 - s.length() - 7));
					}
				});

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		final PackageManager pm = getPackageManager();
		final Spinner spinner = (Spinner) findViewById(R.id.shareTypeSpinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		String selectedItem = BCBSharedPrefUtils
				.getShareSettings(getApplicationContext());
		int selectedPos = -1;
		for (ResolveInfo info : matches) {
			adapter.add(info.loadLabel(pm));
			if (selectedItem.equals(info.loadLabel(pm))) {
				selectedPos = matches.indexOf(info);
			}
		}
		spinner.setAdapter(adapter);

		if (selectedPos != -1) {
			spinner.setSelected(true);
			spinner.setSelection(selectedPos);
		}
		((TextView) findViewById(R.id.charsLeftTextView))
				.setText("Chars left: 140");
		((Button) findViewById(R.id.button1))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						int appSelectedPos = spinner.getSelectedItemPosition();
						ResolveInfo info = matches.get(appSelectedPos);
						intent.setClassName(info.activityInfo.packageName,
								info.activityInfo.name);

						BCBSharedPrefUtils.setShareSettings(
								getApplicationContext(),
								(String) info.loadLabel(pm));
						intent.putExtra(Intent.EXTRA_TEXT,
								((EditText) findViewById(R.id.editText1))
										.getText().toString() + " #bcb11");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
						startActivity(intent);
						finish();
					}
				});

	}
}
