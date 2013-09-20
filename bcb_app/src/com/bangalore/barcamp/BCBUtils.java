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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bangalore.barcamp.activity.AboutActivity;
import com.bangalore.barcamp.activity.BCBActivityBaseClass;
import com.bangalore.barcamp.activity.InternalVenueMapActivity;
import com.bangalore.barcamp.activity.ScheduleActivity;
import com.bangalore.barcamp.activity.SettingsActivity;
import com.bangalore.barcamp.activity.ShareActivity;
import com.bangalore.barcamp.activity.UpdateMessagesActivity;
import com.bangalore.barcamp.activity.WebViewActivity;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.BarcampUserScheduleData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.slidingmenu.lib.SlidingMenuActivity;

public class BCBUtils {

	private static final int MAX_LOG = 2000;
	// private static final String BARCAMP_SCHEDULE_JSON =
	// "http://barcampbangalore.org/schadmin/android.json";
	private static final String BARCAMP_SCHEDULE_JSON = "http://barcampbangalore.org/schadmin/android_bcb14.json";
	private static final String BCB_LOCATION_MAPS_URL = "https://www.google.co.in/maps?t=m&cid=0x9ed33f443055ef5f&z=17&iwloc=A";
	protected static final int START_SCHEDULE = 100;
	protected static final int START_ABOUT = 101;
	protected static final int START_SETTINGS = 102;
	protected static final int START_SHARE = 103;
	protected static final int START_BCB12_TWEETS = 104;
	protected static final int START_BCB_UPDATES = 105;
	private static final String BCB_USER_SCHEDULE_URL = "http://barcampbangalore.org/bcb/wp-android_helper.php?action=getuserdata&userid=%s&userkey=%s";
	protected static final int START_INTERNAL_VENUE = 106;

	public static void createActionBarOnActivity(final Activity activity) {
		createActionBarOnActivity(activity, false);
	}

	public static void createActionBarOnActivity(final Activity activity,
			boolean isHome) {
		// ******** Start of Action Bar configuration
		ActionBar actionbar = (ActionBar) activity
				.findViewById(R.id.actionBar1);
		actionbar.setHomeLogo(R.drawable.home);
		actionbar.setHomeAction(new Action() {
			@Override
			public void performAction(View view) {
				((SlidingMenuActivity) activity).toggle();
			}

			@Override
			public int getDrawable() {
				return R.drawable.home;
			}
		});

		actionbar.setTitle(R.string.app_title_text);
		TextView logo = (TextView) activity.findViewById(R.id.actionbar_title);
		Shader textShader = new LinearGradient(0, 0, 0, logo.getHeight(),
				new int[] { Color.WHITE, 0xff999999 }, null, TileMode.CLAMP);
		logo.getPaint().setShader(textShader);
		actionbar.setOnTitleClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		// ******** End of Action Bar configuration

	}

	public static Action createShareAction(Activity activity) {
		IntentAction shareAction = new IntentAction(activity,
				createShareIntent(activity), R.drawable.share_icon);

		return shareAction;
	}

	public static Intent createShareIntent(Activity activity) {
		Intent intent = new Intent(activity, ShareActivity.class);
		return intent;

	}

	public static PendingIntent createPendingIntentForID(Context context,
			String id, int slot, int session) {
		Intent intent = new Intent(context, SessionAlarmIntentService.class);
		intent.putExtra(SessionAlarmIntentService.SESSION_ID, id);
		intent.putExtra(SessionAlarmIntentService.EXTRA_SLOT_POS, slot);
		intent.putExtra(SessionAlarmIntentService.EXTRA_SESSION_POSITION,
				session);
		int idInt = Integer.parseInt(id);
		PendingIntent pendingIntent = PendingIntent.getService(context, idInt,
				intent, PendingIntent.FLAG_ONE_SHOT);
		return pendingIntent;
	}

	public static Boolean updateContextWithBarcampData(Context context) {
		Boolean retVal = false;
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(BARCAMP_SCHEDULE_JSON);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			// /// DEBUG ONLY
			// page =
			// "{ \"status\": \"have stuff\", \"version\": 11, \"slots\": [{ \"type\": \"fixed\", \"startTime\": \"800\", \"endTime\": \"900\", \"name\": \"Registration\", \"id\": 1 }, { \"type\": \"fixed\", \"startTime\": \"900\", \"endTime\": \"930\", \"name\": \"Introduction\", \"id\": 2 }, { \"type\": \"session\", \"startTime\": \"930\", \"endTime\": \"1015\", \"name\": \"Slot 1\", \"id\": 3, \"sessions\": [{ \"id\": 1491, \"title\": \"Lean Startup Principles : An Engineer&#8217;s Perspective\", \"description\": \"We all have heard about Lean Startup by Eric Ries sometime or the other. I would want to discuss it not in terms of what Lean Startup is or not, but how as an Engineer I can consider and use Lean Startup Principles. Do leave a comment, if you want to ask\\/learn something specific <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/lean-startup-principles-an-engineers-perspective\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/lean-startup-principles-an-engineers-perspective\", \"time\": \"9:30AM - 10:15AM\", \"location\": \"Asteroids\", \"presenter\": \"minni\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1524, \"title\": \"Challenges in deploying a mobile-web application in rural areas\", \"description\": \"I will cover various challenges that arise when deploying a mobile-web application in rural, low-resource settings, and how some of them can be addressed. Both technical and non-technical challenges and solutions will be discussed. There is no one good answer, so there is enough room for everyone to share ideas and brainstorm! <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/challenges-in-deploying-a-mobile-web-application-in-rural-areas\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/challenges-in-deploying-a-mobile-web-application-in-rural-areas\", \"time\": \"9:30AM - 10:15AM\", \"location\": \"Battleship\", \"presenter\": \"aramanuj\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1653, \"title\": \"A sip of CoffeeScript\", \"description\": \"CoffeeScript is a programming language that transcompiles to JavaScript. Inspired from Ruby &amp; Python, it enhances JavaScript&#8217;s brevity and readability and adds more sophisticated features. CoffeeScript compiles predictably to JavaScript and programs can be written in fewer lines with no effect on runtime performance. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/a-sip-of-coffeescript\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/a-sip-of-coffeescript\", \"time\": \"9:30AM - 10:15AM\", \"location\": \"Contra\", \"presenter\": \"sivaraj\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1824, \"title\": \"Powering your applications with In Memory databases\", \"description\": \"In this session we will talk about the advantages of in memory databases and\\u00a0columnar storage. \\u00a0We will also show some example demos highlighting the use of in memory concepts. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/in-memor\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/in-memor\", \"time\": \"9:30AM - 10:15AM\", \"location\": \"Diablo\", \"presenter\": \"setujha\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1787, \"title\": \"Linux 101\", \"description\": \"Session on Linux from newbies. Will include installing Linux as a dual boot or as a virtual machine. Will \\u00a0also include some tips and tricks on how to use linux in everyday life &nbsp; Ingredients required: Laptop with atleast 10 GB free space. (yeah, you read it right, 10 GB!) <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/linux-101\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/linux-101\", \"time\": \"9:30AM - 10:15AM\", \"location\": \"Everquest\", \"presenter\": \"sudeep\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 2015, \"title\": \"Erlang &#8211; The Language powers Facebook Chat\", \"description\": \"Agenda * Erlang &#8211; Introduction * Why Erlang? * Who uses Erlang? * Getting Started with Erlang * Language Fundamental + Demo * Intro to Erlang Web Apps + Demo <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/erlang-the-language-powers-facebook-chat\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/erlang-the-language-powers-facebook-chat\", \"time\": \"9:30AM - 10:15AM\", \"location\": \"Fable\", \"presenter\": \"perlsaran\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }] }, { \"type\": \"session\", \"startTime\": \"1030\", \"endTime\": \"1115\", \"name\": \"Slot 2\", \"id\": 4, \"sessions\": [{ \"id\": 1616, \"title\": \"Design Thinking in Action\", \"description\": \"How Design Thinking is changing the innovation landscape and help us to build world class products. The session will have a short exercise to experience on hand what is design thinking. No resources required. Presenters: Karthi, Chandan(Jakashudga) &nbsp; <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/design-thinking-in-action\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/design-thinking-in-action\", \"time\": \"10:30AM - 11:15AM\", \"location\": \"Asteroids\", \"presenter\": \"karthi\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1916, \"title\": \"Google APIs and Google App Engine &#8211; Introduction &amp; Demo\", \"description\": \"Agenda - REST &#8211; Introduction - API Revolution - Google API offerings - API Ecosystem - How to use Google APIs for your requirements? - Google App Engine &#8211; Introduction - Google App Engine &#8211; For Personal Uses - Google App Engine &#8211; For Start-ups - Demo (Google APIs and Google App Engine) - Discussion [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/google-apis-and-google-app-engine-introduction-demo\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/google-apis-and-google-app-engine-introduction-demo\", \"time\": \"10:30AM - 11:15AM\", \"location\": \"Battleship\", \"presenter\": \"siva-sap\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1731, \"title\": \"Massive Open Online Courses\", \"description\": \"Massive Open Online Courses (MOOCs) are taking the education industry by storm. They are bringing the innovation and creativity, so synonymous with the internet boom, into the field of education. Current methods of teaching, which have been followed since the ancient times, are being questioned. And at the head of all of this are some [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/massive-open-online-courses\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/massive-open-online-courses\", \"time\": \"10:30AM - 11:15AM\", \"location\": \"Contra\", \"presenter\": \"vivek-sagar\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1529, \"title\": \"2002:3239:43c3::1 || IPv6 &#8211; What\\/Why\\/How?!\", \"description\": \"IPv6 &#8211; A buzz word or a stale y2k? Definitely not the latter, but what it means to you? What it means to your business? What&#8217;s the ROI? Why do you need it? How to implement it? The high end overview of implementing ipv6. And some finer details and nitty gritties of taking your blog [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/2002323943c31-ipv6-whatwhyhow\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/2002323943c31-ipv6-whatwhyhow\", \"time\": \"10:30AM - 11:15AM\", \"location\": \"Diablo\", \"presenter\": \"anshprat\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1691, \"title\": \"GWTP &#8211; A complete Model-View-Presenter framework to simplify your next GWT project.\", \"description\": \"Introduction to GWT-P. I will be explaining MVP architecture and How it works?. All basics. GWTP (goo-teepee for short), is a collection of components that build up such an architecture.You can pick the components you need or build your new project from the ground up using the entire package. No matter which approach you choose, [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/gwtp-a-complete-model-view-presenter-framework-to-simplify-your-next-gwt-project\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/gwtp-a-complete-model-view-presenter-framework-to-simplify-your-next-gwt-project\", \"time\": \"10:30AM - 11:15AM\", \"location\": \"Everquest\", \"presenter\": \"sameer\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1835, \"title\": \"Exploratory Testing &#8211; An Unconventional Software Testing approach\", \"description\": \"This session shall cover the below New role of Manual Testing in the changing software world Exploratory testing fundamentals The &#8220;Tester like a Tourist&#8221; metaphor Exploratory Testing &#8220;Tours&#8221; &#8211; Landmark, Super Model, Garbage Collector etc. Strategy for Exploratory Test adoption at a large scale testing organization. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/exploratory-testing-an-unconventional-software-testing-approach\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/exploratory-testing-an-unconventional-software-testing-approach\", \"time\": \"10:30AM - 11:15AM\", \"location\": \"Fable\", \"presenter\": \"ajaytikare\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }] }, { \"type\": \"session\", \"startTime\": \"1130\", \"endTime\": \"1215\", \"name\": \"Slot 3\", \"id\": 5, \"sessions\": [{ \"id\": 1687, \"title\": \"Born To Be You &#8211; Ancient India&#8217;s Answer to Corporate Efficiency\", \"description\": \"The western psychological model of the human mind is about 300 years old, while the Indian model of the human mind is over 10000 years old. The sign of quality is something that has stood the test of time. Furthermore, ancient Indian civilizations were known to be very prosperous and are known to have followed [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/born-to-be-you-ancient-indias-answer-to-corporate-efficiency\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/born-to-be-you-ancient-indias-answer-to-corporate-efficiency\", \"time\": \"11:30AM - 12:15AM\", \"location\": \"Asteroids\", \"presenter\": \"raghu25289\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1588, \"title\": \"No SQL- Losing up some Relationship to gain speed\", \"description\": \"In this session I will cover, No SQL stuff. No SQL like Document Storage and Object Storage. Systems like MongoDB, Couchdb, RavenDB etc. Also explain what you get with No SQL and also what you lose. How, can traditional RDBMS or SQL play good with No SQL. Its all about going beyond our traditional comfort [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/no-sql-losing-up-some-relationship-to-gain-speed\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/no-sql-losing-up-some-relationship-to-gain-speed\", \"time\": \"11:30AM - 12:15AM\", \"location\": \"Battleship\", \"presenter\": \"kunjee\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1628, \"title\": \"Javascript bye bye : Working with Dart\", \"description\": \"&gt; Say Javascript bye bye and hello Dart &gt; Application Development using Dart and HTML-5 <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/working-with-dart\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/working-with-dart\", \"time\": \"11:30AM - 12:15AM\", \"location\": \"Contra\", \"presenter\": \"anandvns\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1902, \"title\": \"The other side of the Fence &#8211; Dealing with Hackers and Malwares\", \"description\": \"As Information Security Professionals we are generally at the receiving end, be it hackers, malwares and our own inquisitive employees who are hell bent on breaking the rules. Will discuss what it is like to be in the hotseat, our perspective on Keeping the data safe. Touch some points on: Weakest link Amidst FUD Is [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/the-other-side-of-the-fence-dealing-with-hackers-and-malwares\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/the-other-side-of-the-fence-dealing-with-hackers-and-malwares\", \"time\": \"11:30AM - 12:15AM\", \"location\": \"Diablo\", \"presenter\": \"prax\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1534, \"title\": \"A Grand Unified Theory of Software\", \"description\": \"Have you ever wondered if &#8220;Goto considered harmful&#8221; was anecdotal evidence or actual fact? Or if &#8220;High level languages are more productive than low level ones&#8221; is actually true? Do you use words like flexibility and stability about your code without being able to actually quantify how flexible and stable it actually is? Then this [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/a-grand-unified-theory-of-software\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/a-grand-unified-theory-of-software\", \"time\": \"11:30AM - 12:15AM\", \"location\": \"Everquest\", \"presenter\": \"vinodkd\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1554, \"title\": \"Going beyond Facebook Pages for Photography.\", \"description\": \"Session on sharing your pictures and receiving\\/giving Positive critique for Photographers. Will be talking about various social platforms where photographers share images, sites that provide critiques, portfolio analysis and ideas to expand your third eye. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/going-beyond-facebook-pages-for-photography\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/going-beyond-facebook-pages-for-photography\", \"time\": \"11:30AM - 12:15AM\", \"location\": \"Fable\", \"presenter\": \"ratzzz\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }] }, { \"type\": \"fixed\", \"startTime\": \"1230\", \"endTime\": \"1330\", \"name\": \"Lunch\", \"id\": 6 }, { \"type\": \"fixed\", \"startTime\": \"1330\", \"endTime\": \"1430\", \"name\": \"Techlash\", \"id\": 7 }, { \"type\": \"session\", \"startTime\": \"1430\", \"endTime\": \"1515\", \"name\": \"Slot 4\", \"id\": 8, \"sessions\": [{ \"id\": 1943, \"title\": \"Workshop &#8211; Do It Yourself Skincare and Haircare Tips\", \"description\": \"Our aim at this workshop is to share some simple do it yourself\\u00a0 skincare&amp; hair care tips formulated using plants, herbs, flowers, fruits and vegetables easily available at home. Armed with these tips, you can pamper your skin and hair even at home and find remedies for the modern maladies that plague us all-\\u00a0hair fall,\\u00a0dandruff,\\u00a0frizzy [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/workshop-do-it-yourself-skincare-and-haircare-tips\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/workshop-do-it-yourself-skincare-and-haircare-tips\", \"time\": \"2:30PM - 3:15PM\", \"location\": \"Asteroids\", \"presenter\": \"neetaadappa\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1754, \"title\": \"A cup of Coffee with IN-MEMORY DATABASE.\", \"description\": \"All about Databases and Database Management System. The evolution of databases have a significant progress from the early 1970&#8242;s when it started with &#8216;CODASYL approach&#8217; to so called InMemory database what we have today. This session will take you through a roller-coaster ride starting with the basics to the more advanced concepts of current databases. [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/a-cup-of-coffee-with-in-memory-database\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/a-cup-of-coffee-with-in-memory-database\", \"time\": \"2:30PM - 3:15PM\", \"location\": \"Battleship\", \"presenter\": \"karthikcheers\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1708, \"title\": \"Building Localized Web &#8211; Why and How\", \"description\": \"* Why should web developers, property owners care bit more about localization? * Case study of localization efforts at Wikimedia Foundation projects. * Intro to L20N * Tools to developers for better language support &#8211; Project Milkshake <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/building-localized-web-why-and-how\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/building-localized-web-why-and-how\", \"time\": \"2:30PM - 3:15PM\", \"location\": \"Contra\", \"presenter\": \"srikanth\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1594, \"title\": \"Vertical Scaling made easy through high-performance actors\", \"description\": \"Vertical scaling is today a major issue when writing server code. Threads and locks are the traditional approach to making full utilization of fat (multi-core) computers, but result is code that is difficult to maintain and which to often does not run much faster than single-threaded code. Actors make good use of fat computers but [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/vertical-scaling-made-easy-through-high-performance-actors\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/vertical-scaling-made-easy-through-high-performance-actors\", \"time\": \"2:30PM - 3:15PM\", \"location\": \"Diablo\", \"presenter\": \"laforge49\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1829, \"title\": \"Enterprise mobility: unwire your business\", \"description\": \"According to a recent study, more than 1.19 billion workers \\u2013 34.9% of the global workforce \\u2013 will be using mobile technology by 2013.* Stay ahead of the game. Give your employees the tools they need to handle critical tasks and make informed decisions in real time \\u2013 no matter where they are \\u2013 with [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/enterprise-mobility-unwire-your-business\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/enterprise-mobility-unwire-your-business\", \"time\": \"2:30PM - 3:15PM\", \"location\": \"Everquest\", \"presenter\": \"avid_arvind\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1716, \"title\": \"Right to Education: Myths and Realities\", \"description\": \"There are a lot of myths regarding the Right to Education which is implemented from this academic year and regarding the 25% reservation in private schools. As an education specialist I would like to have a healthy discussion to dispel the common myths. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/right-to-education-myths-and-realities\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/right-to-education-myths-and-realities\", \"time\": \"2:30PM - 3:15PM\", \"location\": \"Fable\", \"presenter\": \"jyotsna\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }] }, { \"type\": \"session\", \"startTime\": \"1530\", \"endTime\": \"1615\", \"name\": \"Slot 5\", \"id\": 9, \"sessions\": [{ \"id\": 1564, \"title\": \"Coding security\", \"description\": \"Secure coding is the practice of writing programs that are resistant to attack by malicious or mischievous people or programs. Secure coding helps protect a user\\u2019s data from theft or corruption. In addition, an insecure program can provide access for an attacker to take control of a server or a user\\u2019s computer, resulting in anything [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/coding-security\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/coding-security\", \"time\": \"3:30PM - 4:15PM\", \"location\": \"Asteroids\", \"presenter\": \"neeraj0401\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1735, \"title\": \"Bringing the Clouds together : The future of Cloud Services\", \"description\": \"Cloud Services suffer from the same issues that bothered the traditional software world. Customers looking to adopt Cloud services are fearful of vendor lock-in, proprietary platforms and integration challenges. This problem is among the single biggest reason that haunt enterprises and startups looking to exploit Cloud Services. In this session, we will look at the [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/bringing-the-clouds-together-the-future-of-cloud-services\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/bringing-the-clouds-together-the-future-of-cloud-services\", \"time\": \"3:30PM - 4:15PM\", \"location\": \"Battleship\", \"presenter\": \"vivekjuneja\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1433, \"title\": \"Google Penguin Recovery\", \"description\": \"Google\\u2019s Penguin update specifically targets sites that are violating Google Webmaster quality guidelines. Join us If your site experienced a hit by Google Penguin. In order to recover from Google\\u2019s Penguin update I provide various Tips which has been implemented by the top leaders of the Online Industries <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/google-penguin-recovery\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/google-penguin-recovery\", \"time\": \"3:30PM - 4:15PM\", \"location\": \"Contra\", \"presenter\": \"shankarsoma\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1854, \"title\": \"Power Presentations\", \"description\": \"You may have the best of content to be presented the next day, but do end up making the best of slides? There are often multiple ways in which you can present a certain set of information on your slides. Through this session I&#8217;ll try my best to show you how to grasp multiple perspectives [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/power-presentations\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/power-presentations\", \"time\": \"3:30PM - 4:15PM\", \"location\": \"Diablo\", \"presenter\": \"aneet\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1797, \"title\": \"Mobile Enterprise Applications : Performance Know-How\", \"description\": \"\\uf0a7 The tablet industry is expected to grow 38% between now and 2015, growing from 67 million units to 248.6, according to Transparency Marketing Research. \\uf0a7 According to a recent study, more than 1.19 billion workers \\u2013 34.9% of the global workforce \\u2013 will be using mobile technology by 2013. This will create a lot [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/mobile-enterprise-applications-performance-know-how\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/mobile-enterprise-applications-performance-know-how\", \"time\": \"3:30PM - 4:15PM\", \"location\": \"Everquest\", \"presenter\": \"mohankumar\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1547, \"title\": \"Jekyll, Github pages and blogging to Nirvana\", \"description\": \"Find out what the hell is so awesome about Jekyll. It lets you create static and blog aware sites in a simple and customizable way and to top it up you can host it for free on github pages. Session would cover basics of git(bare minimum) and an intro to Jekyll and a demo on [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/jekyll-github-pages-and-blogging-to-niravana\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/jekyll-github-pages-and-blogging-to-niravana\", \"time\": \"3:30PM - 4:15PM\", \"location\": \"Fable\", \"presenter\": \"djds4rce\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }] }, { \"type\": \"session\", \"startTime\": \"1630\", \"endTime\": \"1715\", \"name\": \"Slot 6\", \"id\": 10, \"sessions\": [{ \"id\": 1622, \"title\": \"Advanced JavaScript Techniques\", \"description\": \"The session will cover some JavaScript programming techniques that tend to be overlooked but are useful nonetheless. We will talk about functions, OO in JS, inheritance, reflection, closures, JSON and JS debugging or as much as we have time for! The idea is to have an interactive session geeking out on specific aspects of the [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/advanced-javascript-techniques\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/advanced-javascript-techniques\", \"time\": \"4:30PM - 5:15PM\", \"location\": \"Asteroids\", \"presenter\": \"avranju\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1520, \"title\": \"Develope Websites in Few Mins using Joomla\", \"description\": \"This session is for beginners who want to know about Content Management Systems and specifically Joomla. It contains a small presentation about basics of joomla and how you can develope websites very quickly using it. After that there will be a demo to create a website using joomla, adding some templates, modules, components etc in [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/develope-websites-in-few-mins-using-joomla\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/develope-websites-in-few-mins-using-joomla\", \"time\": \"4:30PM - 5:15PM\", \"location\": \"Battleship\", \"presenter\": \"immanish4u\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1847, \"title\": \"Openstack: Open source software for building private and public clouds.\", \"description\": \"Openstack is Open source software for building private and public clouds. Within 2 years it has over 3000 contributors and 180 plus enterprise participation. Its written in Python which makes it easy for newbies participate with the project. Session outline : 1. Introduction to Openstack 2. Technical Overview 3. Used cases 4. How to contribute [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/openstack-open-source-software-for-building-private-and-public-clouds\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/openstack-open-source-software-for-building-private-and-public-clouds\", \"time\": \"4:30PM - 5:15PM\", \"location\": \"Contra\", \"presenter\": \"koolhead17\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1970, \"title\": \"How to design a good Logo\", \"description\": \"Designing a good logo is not a simple task . Broad step in logo design process would be formulating concept, doing initial sketch, finalizing the logo concept, deciding the theme colors and format. We will learn the process with a hands on exercise. Get a paper and a pencil. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/logo-design\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/logo-design\", \"time\": \"4:30PM - 5:15PM\", \"location\": \"Diablo\", \"presenter\": \"arun-jpeg\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1507, \"title\": \"impress.js &#8211; Whoever said presentations have to be boring\", \"description\": \"There have been enough Dilbert jokes about how boring presentations are. Presentation styles have barely changed in the past 20 years inspite of improvements in available technology. It is now time to throw the slide deck out of the window, and learn to tell a story. I will be covering the basics of how to [...] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/impress-js-whoever-said-presentations-have-to-be-boring\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/impress-js-whoever-said-presentations-have-to-be-boring\", \"time\": \"4:30PM - 5:15PM\", \"location\": \"Everquest\", \"presenter\": \"ninadsp\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }, { \"id\": 1801, \"title\": \"Effective Load Testing Analysis using Loadrunner and Wily introscope\", \"description\": \"Introduction to Load Testing What is Load Testing ? Objective Load Testing Process Planning Scripting using Load Runner Load test execution &amp; Analysis Extended analysis using CA Wily Introsocpe Introduction to Wily Introscope Architecture Analysis Process Key Benefits <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/effective-load-testing-analysis-using-loadrunner-and-wily-intorscope\\\">Read more<\\/a>\", \"permalink\": \"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb12\\/effective-load-testing-analysis-using-loadrunner-and-wily-intorscope\", \"time\": \"4:30PM - 5:15PM\", \"location\": \"Fable\", \"presenter\": \"ganesh_barcamp\", \"photo\":\"http://1.gravatar.com/avatar/bb6caa13742a331aad8b493034663a64?s=100&d=wavatar&r=G\" }] }, { \"type\": \"fixed\", \"startTime\": \"1730\", \"endTime\": \"1815\", \"name\": \"Feedback\", \"id\": 11 }] }";

			// page =
			// "{\"status\":\"have stuff\",\"version\":21,\"slots\":[{\"type\":\"fixed\",\"startTime\":\"800\",\"endTime\":\"900\",\"name\":\"Registration\",\"id\":1},{\"type\":\"fixed\",\"startTime\":\"900\",\"endTime\":\"930\",\"name\":\"Introduction\",\"id\":2},{\"type\":\"session\",\"startTime\":\"930\",\"endTime\":\"1015\",\"name\":\"Slot 1\",\"id\":3,\"sessions\":[{\"id\":2752,\"title\":\"Introduction to Browser Internals\",\"description\":\"You might have written lots of HTML, CSS and Javascript code. Have you ever wondered how it works together? This session focuses on the basics of Internal DOM Structure Layout Engine JavaScript Engine Various Developer Tools available in the browsers How to access\\/change the DOM objects and much more fun demos&#8230;. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-browser-internals\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-browser-internals\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Asteroids\",\"presenter\":\"sivaa\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/f8dc8fabdbce2f177e3f5029775a0587?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2838,\"title\":\"Little Known Secrets to convey anything &amp; everything Visually !!\",\"description\":\"Whether you are an entrepreneur (or) a techie working for a company (or) a Manager handling a team (or) a student, its really important that you communicate effectively to your audience. Text is diminishing. The most successful ones &#8211; right from Marketing Guru Seth Godin, to Search Major Google, have reduced the usage of text [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/little-known-secrets-to-convey-anything-everything-visually\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/little-known-secrets-to-convey-anything-everything-visually\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Battleship\",\"presenter\":\"rameshkumarv\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/c924a8e4258efbc7d88797bd26444e1a?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2952,\"title\":\"Smartphone In India: Durby Of Two OSs\",\"description\":\"India is the second largest mobile market by subscribers and third by handset units. The market, though, is largely dominated by Google Android and Apple is making every effort possible to eat up Samsung&#8217;s share, are Indian App developers really making money from Android? Or, its time to shift gear to grow towards iOS? Be [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/smartphone-in-india-durby-of-two-os\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/smartphone-in-india-durby-of-two-os\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Contra\",\"presenter\":\"Amit Misra\",\"photo\":null},{\"id\":2928,\"title\":\"lend me your processors and I will give you a happier world!\",\"description\":\"by harvesting the power of grid computing, you too can help in finding solution to big, global problems of the world like \\u2022AIDS \\u2022Childhood Cancer \\u2022finding sources for Clean Energy and clean Water \\u2022finding cure for Schistosoma and many more. all you have to do is donate the unused power of your processor! join me [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/lend-me-your-processors-and-i-will-give-you-a-happier-world\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/lend-me-your-processors-and-i-will-give-you-a-happier-world\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Diablo\",\"presenter\":\"ruchir89\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/0a7d20e6af8fe6cf3285fd4ff911e8d6?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2769,\"title\":\"Does India need an EMR?\",\"description\":\"HealthCare is a fundamental problem across all countries in the world. In India, HealthCare has been not been seeing it&#8217;s due focus from the Government for a long time and Private HealthCare is booming. Technology Enthusiasts are creating new applications\\/software for providing better care. But, what is India really looking for? Are we ready for [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/does-india-need-an-emr\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/does-india-need-an-emr\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Everquest\",\"presenter\":\"harinathpv\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/7537a31d187d14025fd9fd7f98ae9009?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":3017,\"title\":\"Is Business a Dubious Move?\",\"description\":\"Interested in Business Game? You Think you are capable of starting Business? How an employee can become an Entrepreneur! Business Formula. Is that so complex?! Steps to initiate your Business Idea! Conclusion <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/is-business-a-dubious-move\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/is-business-a-dubious-move\",\"time\":\"9:30AM - 10:15AM\",\"location\":\"Fable\",\"presenter\":\"Jay Prasaand S\",\"photo\":null}]},{\"type\":\"session\",\"startTime\":\"1030\",\"endTime\":\"1115\",\"name\":\"Slot 2\",\"id\":4,\"sessions\":[{\"id\":2852,\"title\":\"Big Data Basics\",\"description\":\"Big Data, Data Science &amp; Analytics. Data Analytics Life cycle. Analytics technology &amp; tools \\u2013 Map Reduce, Hadoop. Methods to apply this knowledge to real work business challenges. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/big-data-basics\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/big-data-basics\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Asteroids\",\"presenter\":\"Viji\",\"photo\":null},{\"id\":2799,\"title\":\"Making Music with Free Software Tools\",\"description\":\"Ever wanted to create some cool music or tunes? Let&#8217;s do this! Software Instruments: \\u00a0Audacity and Hydrogen&#8230; and some web apps and Chrome extensions. No expertise of\\u00a0\\u00a0Sa Re Ga Ma Pa needed. What to Bring: \\u00a0Laptop or Smartphone. Any tune you are\\/were\\/have been humming. Level: Beginner <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/making-music-with-free-software-tools\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/making-music-with-free-software-tools\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Battleship\",\"presenter\":\"Vivekk\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/4c776f36af429e9382ea60c3e43d6cb6?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2863,\"title\":\"How to Question Your Goverment\",\"description\":\"How to use technology and available laws to question your government for public good. Discussion based session. Its\\u00a0discussion\\u00a0based. I will seed the discussion with ideas and solutions, but everybody can do so We will look at the\\u00a0existing\\u00a0models like writing or calling your representative, RTI, PIL Why should you write to your\\u00a0representative, how, where to send [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/how-to-question-your-goverment\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/how-to-question-your-goverment\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Contra\",\"presenter\":\"thej\",\"photo\":null},{\"id\":2974,\"title\":\"Selenium Simple Test &#8211; a python web framework for functional Testing\",\"description\":\"To write functional test cases for web app, selenium is one of the well known tool across the industry. SST or Selenium Simple Test is framework, written on top of Selenium Web driver, which uses python to write test cases. This is how, one can avoid using Java and still can write efficient web automation <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/selenium-simple-test-a-python-web-framework-for-functional-testing\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/selenium-simple-test-a-python-web-framework-for-functional-testing\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Diablo\",\"presenter\":\"fagun\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/0e05524b54bbb749c0c29087f09ef618?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":3142,\"title\":\"Finding patterns\",\"description\":\"Every thing in this world is related. This is hypothesis. My talk is about why I think every thing in this world is related. How is big data related in finding patterns. I will also speak on why I think data science has numerous possibilities. Then it&#8217;s on what is big data.Why learn it. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/finding-patterns\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/finding-patterns\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Everquest\",\"presenter\":\"Vundemodalu Manjush\",\"photo\":null},{\"id\":3118,\"title\":\"Accessibility  Beyond Guidelines\",\"description\":\"WCAG has become synonym of Accessibility but accessibility testing is more than guidelines. We should remember guidelines are just advice statement and not the Ru!es. The session is about conducting accessibility testing beyond guidelines. If you care about accessibility and don&#8217;t believe in guidelines or any ru!es, this session is for you. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/accessibility-beyond-guidelines\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/accessibility-beyond-guidelines\",\"time\":\"10:30AM - 11:15AM\",\"location\":\"Fable\",\"presenter\":\"Mohit Verma\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/a67daf4e277bef23880480eca1478449?s=96&amp;d=wavatar&amp;r=G\"}]},{\"type\":\"session\",\"startTime\":\"1130\",\"endTime\":\"1215\",\"name\":\"Slot 3\",\"id\":5,\"sessions\":[{\"id\":2785,\"title\":\"5 things to remember when approaching a non-techie girl :)\",\"description\":\"Girls, especially non-techie ones, often get intimidated by technology. So, what are the 5 main things to keep in mind when talking to them, hoping that they will go out for coffee with you!! <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/5-things-to-remember-when-approaching-a-non-techie-girl\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/5-things-to-remember-when-approaching-a-non-techie-girl\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Asteroids\",\"presenter\":\"pujajain\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/6718444d51a2969ece942c2b090de01e?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2812,\"title\":\"Ember.js: A framework for creating ambitious web applications\",\"description\":\"Ember.js is designed to help developers build ambitiously large web applications that are competitive with native apps.\\u00a0This is an introductory session to Ember.js with a simple demo application. Why client side MVC framework (Ember.js, Angular.js) over server side rendered HTML? When should you use Ember.js &amp; when should you not? How fast is client side [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/ember-js-a-framework-for-creating-ambitious-web-applications\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/ember-js-a-framework-for-creating-ambitious-web-applications\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Battleship\",\"presenter\":\"gauthamns\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/9e6def4895a9697d59e65dcde9f26349?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2894,\"title\":\"Bootstrapping a Community Wireless Network\",\"description\":\"The session shall include: 1. Rationale behind building a CWN 2. Basic building blocks of CWN 3. The first link: Fun and challenges 4. Few first hand experiences 5. Q&amp;A <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/bootstrapping-a-community-wireless-network\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/bootstrapping-a-community-wireless-network\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Contra\",\"presenter\":\"Sukant Kole\",\"photo\":null},{\"id\":3072,\"title\":\"Internet of Things: Consumer &amp; Enterprise Applications\",\"description\":\"In a brief 20 minute talk, I will run through the gamut of connected devices that are shaping the immediate and long term future of the internet of things. Not only will I cover the popular consumer stuff relating to home automation and IFTTT, but will also go over the world of the industrial internet [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/internet-of-things-consumer-enterprise-applications\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/internet-of-things-consumer-enterprise-applications\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Diablo\",\"presenter\":\"Sahil Kini\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/68289391ca16fe373cce2314328312ba?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2911,\"title\":\"Profiling and Benchmarking Devices\",\"description\":\"For starters, lets talk about what is profiling and benchmarking, why you need it, what those numbers mean which you get after profiling and most important: with current and future devices bumped up with more processor power and extra memory, how do we compare and set benchmarking standards. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/profiling-and-benchmarking-devices\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/profiling-and-benchmarking-devices\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Everquest\",\"presenter\":\"Daaku\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/7d8d90724c549fd22e200393932099b5?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2840,\"title\":\"Build SIEM with FOSS tools\",\"description\":\"This talk explains one of the ways to build an Security Incident and Event Management (SIEM) solution with FOSS tools. Also, this will cover: * Things to consider while building a SIEM solution * Some lessons learnt * Pros and cons of using it versus buying a commercial solution <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/build-siem-with-foss-tools\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/build-siem-with-foss-tools\",\"time\":\"11:30AM - 12:15AM\",\"location\":\"Fable\",\"presenter\":\"prathap\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/81bac959fcacec4ba50267af482558e5?s=96&amp;d=wavatar&amp;r=G\"}]},{\"type\":\"fixed\",\"startTime\":\"1230\",\"endTime\":\"1330\",\"name\":\"Lunch\",\"id\":6},{\"type\":\"fixed\",\"startTime\":\"1330\",\"endTime\":\"1430\",\"name\":\"Techlash\",\"id\":7},{\"type\":\"session\",\"startTime\":\"1430\",\"endTime\":\"1515\",\"name\":\"Slot 4\",\"id\":8,\"sessions\":[{\"id\":2802,\"title\":\"HTML5 Game Development\",\"description\":\"HTML5 for game development is no longer a possibility but an inevitability as these games work across devices. I&#8217;ll talk about how to create high performance HTML5 games. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/html5-game-development\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/html5-game-development\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Asteroids\",\"presenter\":\"sivaraj\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/afd8a68e15ba2d836e16f795607d87d2?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2731,\"title\":\"How to keep Android open source\",\"description\":\"We keep hearing that Android is not really open source. The code of it available for all after its released. But there is a catch now. Google Play services. Google is moving a lot of its code, which could have been part of Android OS to Play services. Things like Location API&#8217;s, GCM, Games etc. [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/how-to-keep-android-open-source\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/how-to-keep-android-open-source\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Battleship\",\"presenter\":\"the100rabh\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/bb6caa13742a331aad8b493034663a64?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2767,\"title\":\"Product Management ABC and D\",\"description\":\"Intend to keep this session interactive, and primarily driven by participants. Go ahead and shoot your queries. And like they say, there are no stupid questions! Target Audience &#8211; Anyone with an interest in Product Management, be it aspirants, practitioners, or anyone with general interest are all welcome. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/product-management-abc-and-d\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/product-management-abc-and-d\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Contra\",\"presenter\":\"mtanwani\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/2b641429191e93db0ff15d6dccda76d8?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2992,\"title\":\"Meet Open Source Portal and Integration\",\"description\":\"Introduction to open source portals like Liferay and integration of related frameworks and application, rapid development of portals in java. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/meet-open-source-portal-and-integration\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/meet-open-source-portal-and-integration\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Diablo\",\"presenter\":\"chandansharma\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/e889c5b6753c71361398ebd89ca06d1c?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":3027,\"title\":\"Cooking your dev. &amp; prod. infrastructure in minutes with Chef\",\"description\":\"Managing servers and production deployments is part and parcel of today&#8217;s developer activity. In this session we will i. Introduction to Vagrant ii. Introduction to Chef iii. Dev Setup in minutes iv. Live demo of bringing up a basic image to deploy ready. Q&amp;A. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/cooking-infrastructure-with-chef\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/cooking-infrastructure-with-chef\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Everquest\",\"presenter\":\"Ramjee\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/f894e77f943c94d00b1bfa72ccf84191?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":3097,\"title\":\"SWeeTing Cultural Heritage\",\"description\":\"We demonstrate a set of reusable tools that we have developed to help the process of annotation and curation for cultural heritage and museum platforms. We start with customizing a set of tools for a given context and use it to demonstrate its utility in annotating a mural or another artefact with semantic labels. Then [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/sweeting-cultural-heritage\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/sweeting-cultural-heritage\",\"time\":\"2:30PM - 3:15PM\",\"location\":\"Fable\",\"presenter\":\"tbdinesh\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/b49473854236ff992023eea2aa0f1505?s=96&amp;d=wavatar&amp;r=G\"}]},{\"type\":\"session\",\"startTime\":\"1530\",\"endTime\":\"1615\",\"name\":\"Slot 5\",\"id\":9,\"sessions\":[{\"id\":2764,\"title\":\"Corruption control using technology\",\"description\":\"Avoid the corruption and remove the black money concept in India using this technology we can trace all the transaction of the every citizen of the India. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/corruption-control-using-technology\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/corruption-control-using-technology\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Asteroids\",\"presenter\":\"Mahanthesh Shadakshari\",\"photo\":null},{\"id\":2792,\"title\":\"Introduction to Perl Programming\",\"description\":\"Introduction Basic Syntax When to use Perl? Perl Open source community <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-perl-programming\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-perl-programming\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Battleship\",\"presenter\":\"perlsaran\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/6ccdc9d77411ed92f73539dc256dc17e?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2930,\"title\":\"Evaluating startup &amp; technology events!\",\"description\":\"There are more than 100 startup and technology events happening across the country every week with more than a dozen taking place in Bangalore. From an informal meetup to a pitching session to a mighty conference, how to decide which one is worth your time and money? What all parameter should be considered to take [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/evaluating-startup-technology-events-happening-around-you-to-attend\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/evaluating-startup-technology-events-happening-around-you-to-attend\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Contra\",\"presenter\":\"Alok\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/4834adf6a2cc8d5bc3bc0f82e7066422?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2923,\"title\":\"Introduction to Mobile Testing\",\"description\":\"How about getting our app tested before we send it to the store? Join me in understanding the importance of testing a mobile app, and the &#8220;Why&#8221;, &#8220;What&#8221; and &#8220;How&#8221; of testing a Mobile application. It is going to be very informative! So let&#8217;s get started with some of the tips and tricks to test [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-mobile-testing\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-mobile-testing\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Diablo\",\"presenter\":\"abilash\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/656abcb2ce2fa83bc501da062d75fe94?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2976,\"title\":\"Fashion &amp; Technology &#8211; The perfect love.\",\"description\":\"Cloths that react to sound and motion. Crysis style nanotechnology suit. How e-commerce is driving a change in the fashion industry. Do these two fields really make or break each other. \\u00a0Fashion and technology have infinite\\u00a0possibility\\u00a0 lets explore what they hold for us in the near future. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/fashion-technology-the-perfect-love\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/fashion-technology-the-perfect-love\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Everquest\",\"presenter\":\"Bhavish Agarwal\",\"photo\":null},{\"id\":3112,\"title\":\"What the hell are podcasts!!1!\",\"description\":\"Heard about podcasts? No? Maybe? Well, You&#8217;re in the right place!!1! Got a couple of minutes to whine away while you exercise or while you commute to work? Wanna listen to something apart from music? Try podcasts.\\u00a0They are on-demand audio\\/video episodes that you can subscribe to and listen on your mobile device or laptop. Anytime [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/what-the-hell-are-podcasts1\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/what-the-hell-are-podcasts1\",\"time\":\"3:30PM - 4:15PM\",\"location\":\"Fable\",\"presenter\":\"soham\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/ad94879495e21b7362d0d2c845f44bc0?s=96&amp;d=wavatar&amp;r=G\"}]},{\"type\":\"session\",\"startTime\":\"1630\",\"endTime\":\"1715\",\"name\":\"Slot 6\",\"id\":10,\"sessions\":[{\"id\":2925,\"title\":\"Introduction to security testing\\/Ethical Hacking\",\"description\":\"Meet an ethical hacker while you are here at the Bar Camp Bangalore and learn what qualifies for a Hack? What goes on in the mind of an ethical hacker and a hacker? Learn new and the trending technologies to be right there at the right moment. Been Victimized? Learn what you could have done [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-security-testingethical-hacking\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/introduction-to-security-testingethical-hacking\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Asteroids\",\"presenter\":\"nagasahas\",\"photo\":\"http:\\/\\/1.gravatar.com\\/avatar\\/bacfe76c85d8b3efc8ae66c4e505e858?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":2789,\"title\":\"OpenStack in action &#8211; Powering IaaS Cloud\",\"description\":\"Recently OpenStack celebrated 3rd anniversary.Best known for driving cloud innovation and accelerating cloud implementation. OpenStack India user group is emerging as one of the biggest group across globe. This session is purely for beginner to understand basics of OpenStack. - .History and Projects - How to contribute - Success stories Join us and get involved [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/openstack-in-action-powering-iaas-cloud\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/openstack-in-action-powering-iaas-cloud\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Battleship\",\"presenter\":\"Sajid Akhtar\",\"photo\":null},{\"id\":2714,\"title\":\"Participating in Q&amp;A sites and discussion forums\",\"description\":\"There are a whole lot of technical forums and Q&#038;A sites, but from what I&#8217;ve seen the participation is very poor or not quite what is expected. I talk about some ground rules and what you should do to improve your experience and others&#8217; while getting involved in such communities. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/participating-in-qa-sites-2\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/participating-in-qa-sites-2\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Contra\",\"presenter\":\"sathyabhat\",\"photo\":null},{\"id\":2921,\"title\":\"Are we prepared for Full Stack Javascript (MEAN)?\",\"description\":\"The world is moving towards smaller, cleaner and faster pieces of Code. And Yay, Javascript offers all the three. But when building an Enterprise Application are we ready to fit in our Full Stack Javascript. The gung ho around the various Frameworks available in the market and does Javascript eliminate the long existence of Other [&hellip;] <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/are-we-prepared-for-full-stack-javascript-mean\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/are-we-prepared-for-full-stack-javascript-mean\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Diablo\",\"presenter\":\"theChinmay\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/cc651a9ae51768f4164d7bbaa184a119?s=96&amp;d=wavatar&amp;r=G\"},{\"id\":3087,\"title\":\"How to Design a good User Interfaces\",\"description\":\"We ll taking up a real world problem and try design a UI with UX best practices. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/design-user-interfaces\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/design-user-interfaces\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Everquest\",\"presenter\":\"arunmaroon\",\"photo\":null},{\"id\":2934,\"title\":\"Value What you throw!\",\"description\":\"&#8220;Trash is India\\u2019s plague&#8221; While a lot of us know that it is important to manage our trash, only a few of us practice this. This is going to be a talk about why &amp; how as individuals we can stop poisoning our &#8220;Mother earth&#8221;. <a href=\\\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/value-what-you-throw\\\">Read more<\\/a>\",\"permalink\":\"http:\\/\\/barcampbangalore.org\\/bcb\\/bcb14\\/value-what-you-throw\",\"time\":\"4:30PM - 5:15PM\",\"location\":\"Fable\",\"presenter\":\"bhoomika\",\"photo\":\"http:\\/\\/0.gravatar.com\\/avatar\\/29f7e75063d9b0846f88c8670784ed97?s=96&amp;d=wavatar&amp;r=G\"}]},{\"type\":\"fixed\",\"startTime\":\"1730\",\"endTime\":\"1815\",\"name\":\"Feedback\",\"id\":11}]}";
			// /// END OF DEBUG
			if (page.length() > MAX_LOG) {
				int iCount = 0;
				for (; iCount < page.length() - MAX_LOG; iCount += MAX_LOG) {
					Log.e("bcbdata", page.substring(iCount, iCount + MAX_LOG));
				}
				Log.e("bcbdata", page.substring(iCount));
			} else {
				Log.e("bcbdata", page);
			}
			BarcampData data = DataProcessingUtils.parseBCBJSON(page);
			if (data != null) {
				((BarcampBangalore) context).setBarcampData(data);
				retVal = true;
				BCBSharedPrefUtils.setAllBCBUpdates(context, page);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (!retVal) {
			updateContextDataFromSharedPreferences(context);
		}
		return retVal;
	}

	public static void updateContextDataFromSharedPreferences(Context context) {
		try {
			String page = BCBSharedPrefUtils.getAllBCBUpdates(context, null);
			if (page != null) {
				BarcampData data;
				data = DataProcessingUtils.parseBCBJSON(page);
				((BarcampBangalore) context).setBarcampData(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void addNavigationActions(
			final BCBActivityBaseClass homeActivity) {
		homeActivity.setBehindContentView(R.layout.navigation_menu);
		int offset = 100;
		DisplayMetrics metrics = new DisplayMetrics();
		homeActivity.getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		offset = ((metrics.widthPixels * 130)) / 480;

		homeActivity.setBehindOffset(offset);
		homeActivity.setBehindScrollScale(0.5f);

		View view = homeActivity.findViewById(R.id.nav_agenda);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ScheduleActivity.class);
				homeActivity.startActivityForResult(intent, START_SCHEDULE);

			}
		});

		view = homeActivity.findViewById(R.id.nav_about);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, AboutActivity.class);
				homeActivity.startActivityForResult(intent, START_ABOUT);
			}
		});
		view = homeActivity.findViewById(R.id.nav_internal_venue_map);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity,
						InternalVenueMapActivity.class);
				homeActivity.startActivityForResult(intent,
						START_INTERNAL_VENUE);
			}
		});

		view = homeActivity.findViewById(R.id.nav_settings);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, SettingsActivity.class);
				homeActivity.startActivityForResult(intent, START_SETTINGS);
			}
		});
		view.setVisibility(View.GONE);
		view = homeActivity.findViewById(R.id.nav_share);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ShareActivity.class);
				homeActivity.startActivityForResult(intent, START_SHARE);
			}
		});
		view = homeActivity.findViewById(R.id.nav_tweets);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, WebViewActivity.class);
				intent.putExtra(WebViewActivity.URL,
						"file:///android_asset/bcb11_updates.html");
				homeActivity.startActivityForResult(intent, START_BCB12_TWEETS);
			}
		});
		view = homeActivity.findViewById(R.id.nav_update);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity,
						UpdateMessagesActivity.class);
				homeActivity.startActivityForResult(intent, START_BCB_UPDATES);
			}
		});
		view = homeActivity.findViewById(R.id.nav_venue);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final PackageManager pm = homeActivity.getPackageManager();

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(BCB_LOCATION_MAPS_URL));
				final List<ResolveInfo> matches = pm.queryIntentActivities(
						intent, 0);
				for (ResolveInfo info : matches) {
					Log.e("MapPackage", info.loadLabel(pm) + " "
							+ info.activityInfo.packageName + " "
							+ info.activityInfo.name);
					if (info.activityInfo.name
							.equals("com.google.android.maps.MapsActivity")) {
						intent.setClassName("com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity");
					}
				}

				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				homeActivity.startActivity(intent);
			}
		});
		view = homeActivity.findViewById(R.id.nav_BCB);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://barcampbangalore.org"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				homeActivity.startActivity(intent);
			}
		});

	}

	public static void syncUserScheduleData(Context context) {
		String userID = BCBSharedPrefUtils.getUserID(context);
		String userKey = BCBSharedPrefUtils.getUserKey(context);
		if (TextUtils.isEmpty(userKey) || TextUtils.isEmpty(userID)) {
			return;
		}
		Boolean retVal = false;
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			String userScheduleURL = String.format(BCB_USER_SCHEDULE_URL,
					userID, userKey);
			Log.e("UserURL", userScheduleURL);
			HttpUriRequest request = new HttpGet(userScheduleURL);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			if (page.length() > MAX_LOG) {
				int iCount = 0;
				for (; iCount < page.length() - MAX_LOG; iCount += MAX_LOG) {
					Log.e("schedule", page.substring(iCount, iCount + MAX_LOG));
				}
				Log.e("schedule", page.substring(iCount));
			} else {
				Log.e("schedule", page);
			}
			List<BarcampUserScheduleData> data = DataProcessingUtils
					.parseBCBScheduleJSON(page);
			((BarcampBangalore) context).setUserSchedule(data);

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void removeSessionFromSchedule(Context context,
			String sessionid, int slotPos, int sessionPos) {
		BCBSharedPrefUtils.setAlarmSettingsForID(context, sessionid,
				BCBSharedPrefUtils.ALARM_NOT_SET);
		PendingIntent intent = BCBUtils.createPendingIntentForID(context,
				sessionid, slotPos, sessionPos);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(intent);
	}

	public static void setAlarmForSession(Context context, Slot slot,
			Session session, int slotpos, int sessionpos) {
		BCBSharedPrefUtils.setAlarmSettingsForID(context, session.id,
				BCBSharedPrefUtils.ALARM_SET);
		PendingIntent intent = BCBUtils.createPendingIntentForID(context,
				session.id, slotpos, sessionpos);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		int hour = slot.startTime / 100;
		int mins = slot.startTime % 100;
		Log.e("Session", "hour : " + hour + " mins :" + mins);
		GregorianCalendar date = new GregorianCalendar(2013,
				Calendar.SEPTEMBER, 14, hour, mins);
		long timeInMills = date.getTimeInMillis() - 300000;
		alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMills, intent);
	}
}
