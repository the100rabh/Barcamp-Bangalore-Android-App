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

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;

public class SlotsListAdapter extends ArrayAdapter<Slot> {

	private LayoutInflater layoutInflaterService;
	private Context context;

	public SlotsListAdapter(Context context, List<Slot> slotsArray) {
		super(context, R.layout.slots_list_item, slotsArray);
		this.layoutInflaterService = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		Slot viewObject = (Slot) getItem(position);

		if (convertView == null) {
			convertView = layoutInflaterService.inflate(
					R.layout.slots_list_item, null);
			holder = new ViewHolder();
			holder.time = (TextView) convertView.findViewById(R.id.slot_time);
			holder.title = (TextView) convertView.findViewById(R.id.slot_title);
			holder.desc = (TextView) convertView.findViewById(R.id.slot_desc);
			holder.arrow = (ImageView) convertView
					.findViewById(R.id.imageView1);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (holder.time != null) {
			String outString = String.format("%04d hrs", viewObject.startTime);
			holder.time.setText(outString);
		}
		if (holder.title != null) {
			holder.title.setText(viewObject.name);
		}
		holder.arrow.setVisibility(View.GONE);
		if (viewObject.type.equals(Slot.SESSION)) {
			holder.arrow.setVisibility(View.VISIBLE);
			holder.time
					.setBackgroundResource(R.drawable.slot_time_background_drawable);
		} else {
			holder.time
					.setBackgroundResource(R.drawable.slot_time_fixed_background_drawable);
		}
		if (holder.desc != null) {
			holder.desc.setVisibility(View.GONE);
			String descText = "";
			String seperatorText = "";
			if (viewObject.type.equals("session")) {
				for (Session session : viewObject.sessionsArray) {
					if (BCBSharedPrefUtils.getAlarmSettingsForID(context,
							session.id) == BCBSharedPrefUtils.ALARM_SET) {
						descText += seperatorText + "- \"" + session.title
								+ "\" By " + session.presenter + " @"
								+ session.location;
						seperatorText = "\n";
					}
				}
				if (!TextUtils.isEmpty(descText)) {
					holder.desc.setText(descText);
					holder.desc.setVisibility(View.VISIBLE);
				}
			}
		}
		convertView.forceLayout();
		convertView.setTag(holder);

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		Slot slot = (Slot) getItem(position);
		if ((slot != null) && slot.type.equals(Slot.FIXED)) {
			return false;
		}
		return true;
	}

	private static class ViewHolder {
		TextView time;
		TextView title;
		TextView desc;
		ImageView arrow;
	}
}
