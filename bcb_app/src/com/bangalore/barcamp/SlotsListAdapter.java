package com.bangalore.barcamp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;

public class SlotsListAdapter extends BaseAdapter {

	private LayoutInflater layoutInflaterService;
	private Context context;
	private List<Slot> slotsArray;

	public SlotsListAdapter(Context context, List<Slot> slotsArray) {
		super();
		this.layoutInflaterService = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.slotsArray = slotsArray;
	}

	@Override
	public int getCount() {
		return slotsArray.size();
	}

	@Override
	public Object getItem(int pos) {
		return slotsArray.get(pos);
	}

	@Override
	public long getItemId(int id) {
		return id;
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

		convertView.setTag(holder);

		return convertView;
	}

	private static class ViewHolder {
		TextView time;
		TextView title;
		TextView desc;
	}
}
