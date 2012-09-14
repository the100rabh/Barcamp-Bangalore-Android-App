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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.Html;
import android.util.Log;

import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;

public class DataProcessingUtils {

	private static final String SLOT_TITLE = "name";
	private static final String SLOT_TYPE = "type";
	private static final String BEGIN_TIME = "startTime";
	private static final String SESSIONS = "sessions";

	public static BarcampData parseBCBJSON(String jsonString) {
		JSONObject json;
		Log.e("DataProcessingUtils", jsonString);
		BarcampData data = null;
		try {
			json = (JSONObject) new JSONTokener(jsonString).nextValue();

			String status = json.getString("status");
			if (status.equals("have stuff")) {
				JSONArray jsonSlots = json.getJSONArray("slots");
				data = new BarcampData();
				List<Session> sessionList = null;
				List<Slot> slotList = new ArrayList<Slot>();
				for (int iCount = 0; iCount < jsonSlots.length(); iCount++) {
					JSONObject curJsonSlot = jsonSlots.getJSONObject(iCount);
					Slot slot = new Slot();
					slot.id = iCount;
					slot.name = curJsonSlot.getString(SLOT_TITLE);
					slot.type = curJsonSlot.getString(SLOT_TYPE);
					slot.startTime = curJsonSlot.getInt(BEGIN_TIME);
					slot.pos = iCount;
					if (curJsonSlot.has(SESSIONS)) {
						JSONArray sessions = curJsonSlot.getJSONArray(SESSIONS);
						sessionList = new ArrayList<Session>();
						for (int jCount = 0; jCount < sessions.length(); jCount++) {
							Session session = new Session();
							JSONObject jsonSession = sessions
									.getJSONObject(jCount);
							session.id = jsonSession.getString("id");
							session.location = jsonSession
									.getString("location");
							session.pos = jCount;
							session.presenter = jsonSession
									.getString("presenter");
							session.time = jsonSession.getString("time");
							session.title = Html.fromHtml(
									jsonSession.getString("title")).toString();
							session.description = jsonSession
									.getString("description");
							sessionList.add(session);
						}
						slot.sessionsArray = sessionList;
					}
					slotList.add(slot);
				}
				data.slotsArray = slotList;
				data.status = "";
			} else {
				data = new BarcampData();
				data.status = status;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return data;
	}
}
