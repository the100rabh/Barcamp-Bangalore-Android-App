package com.bangalore.barcamp.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;

public class LocationActivity extends BCBActivityBaseClass {

	public static final String LOCATION_EXTRA = "location_extra";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_screen);

		BCBUtils.addNavigationActions(this);
		BCBUtils.createActionBarOnActivity(this);
		String location = getIntent().getStringExtra(LOCATION_EXTRA)
				.toLowerCase();
		int mapID = getResources().getIdentifier(location, "drawable",
				this.getPackageName());
		if (mapID > 0) {
			int id = R.drawable.asteroids;
			((ImageView) findViewById(R.id.locationImage))
					.setImageResource(mapID);
		}

	}

}
