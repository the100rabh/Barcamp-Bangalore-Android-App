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

package com.bangalore.barcamp;

import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bangalore.barcamp.data.BCBUpdatesMessage;

public class MessagesListAdapter extends ArrayAdapter<BCBUpdatesMessage> {

	private int listViewResource;
	private LayoutInflater layoutInflaterService;

	public MessagesListAdapter(Context context, int listViewResource,
			List<BCBUpdatesMessage> items) {
		super(context, listViewResource, items);
		this.listViewResource = listViewResource;
		this.layoutInflaterService = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		BCBUpdatesMessage message = getItem(position);

		if (convertView == null) {
			convertView = layoutInflaterService.inflate(listViewResource, null);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView
					.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView
					.findViewById(android.R.id.text2);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		if (holder.text1 != null) {
			GregorianCalendar time = new GregorianCalendar();
			time.setTimeInMillis(Long.parseLong(message.getTimestamp()));
			holder.text1
					.setText(DateFormat.format("dd MMM yyyy, h:mmaa", time));
		}
		if (holder.text2 != null) {
			holder.text2.setText(message.getMessage());
		}

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	private static class ViewHolder {

		public TextView text2;

		public TextView text1;

	}

}
