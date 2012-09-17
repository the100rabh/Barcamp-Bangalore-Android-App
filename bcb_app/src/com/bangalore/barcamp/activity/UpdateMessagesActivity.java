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

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.MessagesListAdapter;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BCBUpdatesMessage;
import com.bangalore.barcamp.database.MessagesDataSource;

public class UpdateMessagesActivity extends BCBActivityBaseClass {

	public static final String FROM_NOTIFICATION = "fromNotification";
	public static String EXTRA_POS = "pos";
	MessageLoadingTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slot_details_list);

		BCBUtils.createActionBarOnActivity(this);
		BCBUtils.addNavigationActions(this);
		if (getIntent().getBooleanExtra(FROM_NOTIFICATION, false)) {
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		task = new MessageLoadingTask();
		task.execute();
	}

	class MessageLoadingTask extends
			AsyncTask<Void, Void, List<BCBUpdatesMessage>> {

		@Override
		protected List<BCBUpdatesMessage> doInBackground(Void... params) {
			MessagesDataSource ds = new MessagesDataSource(
					UpdateMessagesActivity.this);
			ds.open();
			List<BCBUpdatesMessage> list = ds.getAllMessages();
			Collections.reverse(list);
			ds.close();
			return list;
		}

		@Override
		protected void onPostExecute(List<BCBUpdatesMessage> result) {
			super.onPostExecute(result);
			ListView listview = (ListView) findViewById(R.id.listView1);
			MessagesListAdapter adapter = new MessagesListAdapter(
					UpdateMessagesActivity.this, R.layout.updates_list_item,
					result);
			listview.setAdapter(adapter);
		}

	}
}
