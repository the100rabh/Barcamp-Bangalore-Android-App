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
	protected void onStop() {
		if (slidingMenu != null) {
			if (slidingMenu.isMenuOpen()) {
				toggle();
			}
		}
		super.onStop();
	}
}
