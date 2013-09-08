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

package com.bangalore.barcamp.data;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class BarcampBangalore extends Application {

	private static BarcampBangalore irApplicationContext;

	private BarcampData barcampData;

	private List<BarcampUserScheduleData> userSchedule;

	public BarcampBangalore() {
		if (irApplicationContext == null) {
			irApplicationContext = this;
		}
	}

	public BarcampData getBarcampData() {
		return barcampData;
	}

	public void setBarcampData(BarcampData data) {
		barcampData = data;
	}

	public List<BarcampUserScheduleData> getUserSchedule() {
		return userSchedule;
	}

	public void setUserSchedule(List<BarcampUserScheduleData> userSchedule) {
		this.userSchedule = userSchedule;
	}

	public void removeSessionFromUserSchedule(String id) {
		if (userSchedule == null) {
			userSchedule = new ArrayList<BarcampUserScheduleData>();
		}
		for (BarcampUserScheduleData data : userSchedule) {
			if (data.id.equals(id)) {
				userSchedule.remove(data);
			}
		}
	}

	public void addSessionToUserSchedule(String id) {
		BarcampUserScheduleData data = new BarcampUserScheduleData();
		data.id = id;
		if (userSchedule == null) {
			userSchedule = new ArrayList<BarcampUserScheduleData>();
		}
		userSchedule.add(data);
	}
}
