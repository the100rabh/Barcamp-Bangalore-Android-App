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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.SlotsListAdapter;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Slot;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

public class ScheduleActivity extends BCBActivityBaseClass {

	private List<Button> buttons = new ArrayList<Button>();
	private FetchScheduleAsyncTask task = null;
	private List<Slot> slotsArray;
	private static final int SHOW_ERROR_DIALOG = 100;
	private static final int HOUR_DISTANCE = 124;
	private static final String BCB_DATA = "BCBData";

	private static float screenAdjustment = 1.0f;

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
					clearExisitingLayout();
				}
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh_icon;
			}
		}, 0);

		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {
			screenAdjustment = 1.5f;
		}

	}

	protected void clearExisitingLayout() {
		((LinearLayout) findViewById(R.id.timeScheduleLayout)).removeAllViews();
		((LinearLayout) findViewById(R.id.scheduleItemsLayout))
				.removeAllViews();
	}

	private void addScheduleItems(List<Slot> slotsArray) {
		ListView listView = (ListView) findViewById(R.id.listView1);
		SlotsListAdapter adapter = new SlotsListAdapter(this, slotsArray);
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

		// boolean isFirst = true;
		// if (slotsArray != null && slotsArray.size() > 0) {
		// LinearLayout layout = (LinearLayout)
		// findViewById(R.id.scheduleItemsLayout);
		// for (Slot slot : slotsArray) {
		// Button button = new Button(this);
		// button.setText(slot.name);
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, getButtonHeight(slot));
		// if (isFirst) {
		// isFirst = false;
		// int topPadding = 10;
		// final float factor = getResources().getDisplayMetrics().density;
		// if (factor == 1.0) {
		// topPadding += ((((slot.startTime % 100) * 100) / 60) * HOUR_DISTANCE)
		// / 100;
		// } else {
		// topPadding += ((((slot.startTime % 100) * 100) / 60) * HOUR_DISTANCE)
		// / 100 + 1;
		// }
		//
		// params.setMargins((int) (60 * screenAdjustment),
		// (int) (topPadding * screenAdjustment),
		// (int) (30 * screenAdjustment), 0);
		// } else {
		// params.setMargins((int) (60 * screenAdjustment), 0,
		// (int) (30 * screenAdjustment), 0);
		// }
		//
		// button.setTextColor(Color.WHITE);
		// if (slot.type.compareToIgnoreCase(Slot.SESSION) == 0) {
		// button.setBackgroundResource(R.drawable.schedule_item_session_button);
		// button.setOnClickListener(new SlotItemClickListener(this,
		// slot));
		// } else if (slot.type.compareToIgnoreCase(Slot.FIXED) == 0) {
		// button.setBackgroundResource(R.drawable.schedule_item_fixed_button);
		// button.setClickable(false);
		// } else {
		// continue;
		// }
		// layout.addView(button, params);
		// buttons.add(button);
		// }
		// }
	}

	private int getButtonHeight(Slot slot) {
		final float factor = getResources().getDisplayMetrics().density;
		int startHours = slot.startTime / 100;
		int startMins = slot.startTime % 100;
		startMins = (startMins * 100) / 60;
		int endHours = slot.endTime / 100;
		int endMins = slot.endTime % 100;
		endMins = (endMins * 100) / 60;
		int start = startHours * 100 + startMins;
		int end = endHours * 100 + endMins;
		int height = 0;
		if (factor == 1.0) {
			height = ((end - start) * HOUR_DISTANCE) / 100;
		} else {
			height = ((end - start) * (HOUR_DISTANCE - 1)) / 100;
		}
		return (int) (height * screenAdjustment);
	}

	private void addScheduleLayouts(int startHours, int endHours) {
		// LinearLayout layout = (LinearLayout)
		// findViewById(R.id.timeScheduleLayout);
		// final float factor = getResources().getDisplayMetrics().density;
		// for (int count = startHours; count <= endHours; count++) {
		// ImageView image = new ImageView(this);
		// image.setImageResource(R.drawable.time_line);
		// image.setMinimumHeight((int) (10 * screenAdjustment));
		// image.setPadding(0, (int) (8 * screenAdjustment), 0, 0);
		// LinearLayout.LayoutParams imageParams = new
		// LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// image.setBackgroundColor(android.R.color.transparent);
		// image.setLayoutParams(imageParams);
		// layout.addView(image);
		// TextView textView = new TextView(this);
		//
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// params.setMargins((int) (5 * screenAdjustment), 0, 0, 0);
		// String timeString = String.valueOf(count);
		// timeString += " hrs";
		// textView.setTextColor(Color.WHITE);
		// if (factor == 1.0) {
		// textView.setTextSize(12 * screenAdjustment);
		// textView.setPadding(0, 0, 0, (int) (94 * screenAdjustment));
		// } else {
		// textView.setTextSize(10);
		// textView.setPadding(0, 0, 0, (int) (86 * screenAdjustment));
		// }
		// textView.setText(timeString);
		// layout.addView(textView, params);
		// }
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
					// findViewById(R.id.scrollView1).setVisibility(View.VISIBLE);
					findViewById(R.id.listView1).setVisibility(View.VISIBLE);
					int startTime = data.slotsArray.get(0).startTime;
					int endTime = data.slotsArray
							.get(data.slotsArray.size() - 1).endTime;
					addScheduleLayouts(startTime / 100, endTime / 100);
					addScheduleItems(data.slotsArray);
				}
				if (!result) {
					// failure
					showDialog(SHOW_ERROR_DIALOG);
				}
			}
			task = null;
		}

	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BCB_DATA,
				((BarcampBangalore) getApplicationContext()).getBarcampData());
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
	protected void onResume() {
		super.onResume();
		BarcampData data = ((BarcampBangalore) getApplicationContext())
				.getBarcampData();
		if (data == null) {
			findViewById(R.id.spinnerLayout).setVisibility(View.VISIBLE);
			// findViewById(R.id.scrollView1).setVisibility(View.GONE);
			findViewById(R.id.listView1).setVisibility(View.GONE);
			task = new FetchScheduleAsyncTask();
			task.execute();
		} else if (((LinearLayout) findViewById(R.id.timeScheduleLayout))
				.getChildCount() == 0) {
			clearExisitingLayout();
			findViewById(R.id.spinnerLayout).setVisibility(View.GONE);
			// findViewById(R.id.scrollView1).setVisibility(View.VISIBLE);
			findViewById(R.id.listView1).setVisibility(View.VISIBLE);
			int startTime = data.slotsArray.get(0).startTime;
			int endTime = data.slotsArray.get(data.slotsArray.size() - 1).endTime;
			addScheduleLayouts(startTime / 100, endTime / 100);
			addScheduleItems(data.slotsArray);
		}
	}
}
