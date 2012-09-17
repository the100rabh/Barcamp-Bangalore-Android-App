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

import android.os.Bundle;
import android.view.KeyEvent;

import com.slidingmenu.lib.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenuActivity;

public class BCBActivityBaseClass extends SlidingMenuActivity {
	protected SlidingMenu slidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		slidingMenu = (SlidingMenu) super.findViewById(R.id.slidingmenulayout);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			toggle();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (slidingMenu != null) {
				if (slidingMenu.isMenuOpen()) {
					toggle();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (slidingMenu != null) {
			if (slidingMenu.isMenuOpen()) {
				toggle();
			}
		}
		super.onStop();
	}
}
