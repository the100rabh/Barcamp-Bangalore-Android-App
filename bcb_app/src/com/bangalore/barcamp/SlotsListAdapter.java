package com.bangalore.barcamp;

import java.util.List;

import android.content.Context;
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
	private List<Slot> slotsArray;

	public SlotsListAdapter(Context context, List<Slot> slotsArray) {
		super(context, R.layout.slots_list_item, slotsArray);
		this.layoutInflaterService = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.slotsArray = slotsArray;
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
		}
		if (holder.desc != null) {
			holder.desc.setVisibility(View.GONE);
			for (Session session : viewObject.sessionsArray) {
				if (BCBSharedPrefUtils.getAlarmSettingsForID(context,
						session.id) == BCBSharedPrefUtils.ALARM_SET) {
					holder.desc.setText(session.title + " By "
							+ session.presenter + " at " + session.location);
					holder.desc.setVisibility(View.VISIBLE);
					break;
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
