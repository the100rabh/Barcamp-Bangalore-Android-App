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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

public class WebViewActivity extends BCBActivityBaseClass {
	private static final int SHOW_ERROR_DIALOG = 100;
	public static final String URL = "URLToShow";
	WebView webView;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String newUrl = intent.getStringExtra(URL);
		String url = getIntent().getStringExtra(URL);
		if (!newUrl.equals(url)) {
			setIntent(intent);
			findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl(newUrl);
			if (slidingMenu.isMenuOpen()) {
				toggle();
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		BCBUtils.createActionBarOnActivity(this);
		BCBUtils.addNavigationActions(this);

		webView = (WebView) findViewById(R.id.webView);
		WebSettings websettings = webView.getSettings();
		websettings.setJavaScriptEnabled(true);
		webView.setClickable(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				findViewById(R.id.linearLayout2).setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				showDialog(SHOW_ERROR_DIALOG);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.equals(getIntent().getStringExtra(URL))) {
					return true;
				}
				Log.e("Action", url);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				return true;
			}

		});

		String url = getIntent().getStringExtra(URL);
		webView.loadUrl(url);

		ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		actionbar.addAction(new Action() {

			@Override
			public void performAction(View arg0) {
				findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
				webView.reload();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		}, 0);

	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		switch (id) {
		case SHOW_ERROR_DIALOG:
			alertDialog.setCancelable(false);
			alertDialog.setTitle(getString(R.string.error_title));
			alertDialog
					.setMessage(getString(R.string.connection_error_and_try_again));
			alertDialog.setButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(SHOW_ERROR_DIALOG);
							WebViewActivity.this.finish();
						}
					});
			break;
		}
		return alertDialog;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
