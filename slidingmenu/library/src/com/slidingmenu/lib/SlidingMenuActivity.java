package com.slidingmenu.lib;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.slidingmenu.lib.CustomViewAbove.LayoutParams;

public class SlidingMenuActivity extends Activity {

	private SlidingMenu mSlidingMenu;
	private View mLayout;
	private boolean mContentViewCalled = false;
	private boolean mBehindContentViewCalled = false;
	private SlidingMenuList mMenuList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.slidingmenumain);
		mSlidingMenu = (SlidingMenu) super.findViewById(R.id.slidingmenulayout);
		mSlidingMenu.registerViews((CustomViewAbove) findViewById(R.id.slidingmenuabove),
				(CustomViewBehind) findViewById(R.id.slidingmenubehind));
		mLayout = super.findViewById(R.id.slidingmenulayout);
	}

	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (!mContentViewCalled || !mBehindContentViewCalled) {
			throw new IllegalStateException("Both setContentView and" +
					"setBehindContentView must be called in onCreate.");
		}
		mSlidingMenu.setStatic(isStatic());
	}

	@Override
	public void setContentView(int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	public void setContentView(View v) {
		setContentView(v, null);
	}

	public void setContentView(View v, LayoutParams params) {
		if (!mContentViewCalled) {
			mContentViewCalled = !mContentViewCalled;
		}
		mSlidingMenu.setAboveContent(v, params);
	}
	
	public void setBehindContentView(int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	public void setBehindContentView(View v) {
		setBehindContentView(v, null);
	}
	
	public void setBehindContentView(View v, LayoutParams params) {
		if (!mBehindContentViewCalled) {
			mBehindContentViewCalled = !mBehindContentViewCalled;
		}
		mSlidingMenu.setBehindContent(v);
	}
	
	private boolean isStatic() {
		return mLayout instanceof LinearLayout;
	}
	
	public int getBehindOffset() {
		// TODO
		return 0;
	}
	
	public void setBehindOffset(int i) {
		mSlidingMenu.setBehindOffset(i);
	}
	
	public float getBehindScrollScale() {
		// TODO
		return 0;
	}
	
	public void setBehindScrollScale(float f) {
		mSlidingMenu.setScrollScale(f);
	}

	@Override
	public View findViewById(int id) {
		return mSlidingMenu.findViewById(id);
	}

	public void toggle() {
//		if (isStatic()) return;
		if (mSlidingMenu.isMenuOpen()) {
			showContent();
		} else {
			showMenu();
		}
	}

	public void showMenu() {
//		if (isStatic()) return;
		mSlidingMenu.showMenu();
	}

	public void showContent() {
//		if (isStatic()) return;
		mSlidingMenu.showContent();
	}

	public void addMenuListItem(MenuListItem mli) {
		mMenuList.add(mli);
	}

	public static class SlidingMenuList extends ListView {
		public SlidingMenuList(final Context context) {
			super(context);
			setAdapter(new SlidingMenuListAdapter(context));
			setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					OnClickListener listener = ((SlidingMenuListAdapter)getAdapter()).getItem(position).mListener;
					if (listener != null) listener.onClick(view);
				}				
			});
		}
		public void add(MenuListItem mli) {
			((SlidingMenuListAdapter)getAdapter()).add(mli);
		}
	}

	public static class SlidingMenuListAdapter extends ArrayAdapter<MenuListItem> {

		public SlidingMenuListAdapter(Context context) {
			super(context, 0);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView != null) {
				v = convertView;
			} else {
				LayoutInflater inflater = 
						(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.slidingmenurow, null);
			}
			MenuListItem item = getItem(position);
			ImageView icon = (ImageView) v.findViewById(R.id.slidingmenurowicon);
			icon.setImageDrawable(item.mIcon);
			TextView title = (TextView) v.findViewById(R.id.slidingmenurowtitle);
			title.setText(item.mTitle);
			return v;
		}
	}

	public class MenuListItem {
		private Drawable mIcon;
		private String mTitle;
		private OnClickListener mListener;
		public MenuListItem(String title) {
			mTitle = title;
		}
		public void setTitle(String title) {
			mTitle = title;
		}
		public void setOnClickListener(OnClickListener listener) {
			mListener = listener;
		}
		public View toListViewRow() {
			View v = SlidingMenuActivity.this.getLayoutInflater().inflate(R.layout.slidingmenurow, null);
			((TextView)v.findViewById(R.id.slidingmenurowtitle)).setText(mTitle);
			((ImageView)v.findViewById(R.id.slidingmenurowicon)).setImageDrawable(mIcon);
			return v;
		}
	}



}
