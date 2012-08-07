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

import static com.bangalore.barcamp.gcm.CommonUtilities.SENDER_ID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.SlotsListAdapter;
import com.bangalore.barcamp.data.BCBUpdatesMessage;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Slot;
import com.bangalore.barcamp.database.MessagesDataSource;
import com.bangalore.barcamp.gcm.GCMUtils;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

public class ScheduleActivity extends BCBActivityBaseClass {

	private FetchScheduleAsyncTask task = null;
	private List<Slot> slotsArray;
	private SlotsListAdapter adapter;
	private static final int SHOW_ERROR_DIALOG = 100;
	private static final String BCB_DATA = "BCBData";
	private static final String LIST_POS = "ListPos";

	AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);

		BCBUtils.createActionBarOnActivity(this);
		BCBUtils.addNavigationActions(this);

		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();

		if (data == null && savedInstanceState != null
				&& savedInstanceState.containsKey(BCB_DATA)) {
			((BarcampBangalore) getApplicationContext())
					.setBarcampData((BarcampData) savedInstanceState
							.getSerializable(BCB_DATA));
		}

		ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		actionbar.addAction(new Action() {

			@Override
			public void performAction(View arg0) {
				if (task == null) {
					findViewById(R.id.spinnerLayout)
							.setVisibility(View.VISIBLE);
					// findViewById(R.id.scrollView1).setVisibility(View.GONE);
					findViewById(R.id.listView1).setVisibility(View.GONE);
					task = new FetchScheduleAsyncTask();
					task.execute();
				}
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		}, 0);

		if (!GCMUtils.isRegistered(this)) {
			Intent registrationIntent = new Intent(
					"com.google.android.c2dm.intent.REGISTER");
			// sets the app name in the intent
			registrationIntent.putExtra("app",
					PendingIntent.getBroadcast(this, 0, new Intent(), 0));
			registrationIntent.putExtra("sender", SENDER_ID);
			startService(registrationIntent);
		}

		MessagesDataSource ds = new MessagesDataSource(getApplicationContext());
		ds.open();
		List<BCBUpdatesMessage> list = ds.getAllMessages();
		ds.close();

		// so db backup here
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data1 = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//" + getPackageName()
						+ "//databases//" + "messages.db";
				String backupDBPath = "messages.db";
				File currentDB = new File(data1, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(this, "Database backup complete",
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {

			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void addScheduleItems(List<Slot> slotsArray) {
		ListView listView = (ListView) findViewById(R.id.listView1);
		adapter = new SlotsListAdapter(this, slotsArray);
		this.slotsArray = slotsArray;
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				if (ScheduleActivity.this.slotsArray.get(pos).type
						.equals(Slot.SESSION)) {
					Intent intent = new Intent(ScheduleActivity.this,
							SlotDetailsActivity.class);
					intent.putExtra(SlotDetailsActivity.EXTRA_POS, pos);
					startActivity(intent);
				}
			}
		});
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		switch (id) {
		case SHOW_ERROR_DIALOG:
			alertDialog.setCancelable(false);
			alertDialog.setTitle("Error!!!");
			alertDialog
					.setMessage("Connection Error!! Check if you have internet connectivity.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(SHOW_ERROR_DIALOG);
				}
			});
			break;
		}
		return alertDialog;
	}

	// protected void onStop()

	public class SlotItemClickListener implements OnClickListener {

		Slot slot;
		Activity context;

		public SlotItemClickListener(Activity context, Slot slot) {
			this.slot = slot;
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, SlotDetailsActivity.class);
			intent.putExtra(SlotDetailsActivity.EXTRA_POS, slot.pos);
			context.startActivity(intent);
		}
	}

	class FetchScheduleAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			return BCBUtils
					.updateContextWithBarcampData(getApplicationContext());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!isCancelled()) {
				BarcampData data = ((BarcampBangalore) getApplicationContext())
						.getBarcampData();
				if (data != null) {
					findViewById(R.id.spinnerLayout).setVisibility(View.GONE);
					findViewById(R.id.listView1).setVisibility(View.VISIBLE);
					addScheduleItems(data.slotsArray);
				}
				if (!result) {
					// failure
					showDialog(SHOW_ERROR_DIALOG);
					findViewById(R.id.progressBar1).setVisibility(View.GONE);
				}
			}
			task = null;
		}

	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BCB_DATA,
				((BarcampBangalore) getApplicationContext()).getBarcampData());
		ListView listView = (ListView) findViewById(R.id.listView1);
		outState.putParcelable(LIST_POS, listView.onSaveInstanceState());
	}

	@Override
	protected void onStop() {
		if (this.task != null) {
			task.cancel(false);
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(false);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		((ListView) findViewById(R.id.listView1))
				.onRestoreInstanceState(savedInstanceState
						.getParcelable(LIST_POS));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (BCBSharedPrefUtils.getScheduleUpdated(this)) {
			((BarcampBangalore) getApplicationContext()).setBarcampData(null);
			BCBSharedPrefUtils.setScheduleUpdated(this, false);
		}
		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();
		if (data == null) {
			findViewById(R.id.spinnerLayout).setVisibility(View.VISIBLE);
			findViewById(R.id.listView1).setVisibility(View.GONE);
			task = new FetchScheduleAsyncTask();
			task.execute();
		} else {
			findViewById(R.id.spinnerLayout).setVisibility(View.GONE);
			findViewById(R.id.listView1).setVisibility(View.VISIBLE);
			addScheduleItems(data.slotsArray);
		}
	}
}
