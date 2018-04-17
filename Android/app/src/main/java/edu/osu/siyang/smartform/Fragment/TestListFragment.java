package edu.osu.siyang.smartform.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import edu.osu.siyang.smartform.Activity.AppEULA;
import edu.osu.siyang.smartform.Activity.HealthActivity;
import edu.osu.siyang.smartform.Activity.InfoActivity;
import edu.osu.siyang.smartform.Activity.IntroActivity;
import edu.osu.siyang.smartform.Activity.TestPagerActivity;
import edu.osu.siyang.smartform.Bean.Test;
import edu.osu.siyang.smartform.Bean.TestLab;
import edu.osu.siyang.smartform.R;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class TestListFragment extends ListFragment {

	public TourGuide mTutorialHandler;

	private static final String TAG = "TestListFragment";

	private ArrayList<Test> mTests;
	private boolean mHintVisible;
	private boolean mAboutVisible;
	private boolean mInfoVisible;
	private Callbacks mCallbacks;
	private LinearLayout mTabIndex, mTabHealth, mTabFind;

	private Button mNewTestButton;
	private Button mAddTestButton;
	private Button mHealthSurveyButton;
	private Button mUserSurveyButton;
	private Button copyId1;
	private Button copyId2;
	private TextView textId1;
	private TextView textId2;
	private ScrollView mHintView1;
	private ScrollView mHintView2;
	private ScrollView mAboutUs1;
	private ScrollView mAboutUs2;
	private LinearLayout mCopyright1;
	private LinearLayout mCopyright2;
	private static String uid = null;
	private FloatingActionButton floatingActionButton;
	private static String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	private SharedPreferences mPref;
	private SharedPreferences.Editor mEditor;

	public TestListFragment() {
	}

	public synchronized static String id(Context context) {
		if (uniqueID == null) {
			SharedPreferences sharedPrefs = context.getSharedPreferences(
					PREF_UNIQUE_ID, Context.MODE_PRIVATE);
			uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
			if (uniqueID == null) {
				uniqueID = UUID.randomUUID().toString();
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString(PREF_UNIQUE_ID, uniqueID);
				editor.commit();
			}
		}
		return uniqueID;
	}

	/**
	 * Required interface for hosting activities
	 */
	public interface Callbacks {
		void onTestSelected(Test test);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		// unchecked cast, so you must document this somewhere!
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach(){
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		// Show EULA
		new AppEULA(getActivity()).show();
		// Tap into the hosting activity and ask it to display the title
		getActivity().setTitle(edu.osu.siyang.smartform.R.string.app_name);
		uid = id(getContext());
		Log.d(TAG, "User id = " + uid);
		//Toast.makeText(getContext(),"User ID: "+ uid,Toast.LENGTH_SHORT).show();
		//  Declare a new thread to do a preference check
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				//  Initialize SharedPreferences
				SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

				//  Create a new boolean and preference and set it to true
				final boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

				//  If the activity has never started before...
				if (isFirstStart) {

					//  Launch app intro
					Intent i = new Intent(getActivity(), IntroActivity.class);
					startActivity(i);

					//  Make a new preferences editor
					SharedPreferences.Editor e = getPrefs.edit();

					//  Edit preference to make it false because we don't want this to run again
					e.putBoolean("firstStart", false);

					//  Apply changes
					e.apply();
				}
			}
		});

		// Start the thread
		t.start();

		// Get singleton and then get list of tests
		mTests = TestLab.get(getActivity()).getTests();

		// Create the Adapter
		TestAdapter adapter = new TestAdapter(mTests);
		setListAdapter(adapter);

		// Remember the info about subtitle visibility
		setRetainInstance(true);
		mHintVisible = true;
		mAboutVisible = true;
		mInfoVisible = true;
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		// Get view for fragment
		View v = inflater.inflate(edu.osu.siyang.smartform.R.layout.fragment_test_list, parent, false);

		// Set custom empty view
		View empty = v.findViewById(edu.osu.siyang.smartform.R.id.custom_empty_view);
		ListView displayList = (ListView) v.findViewById(android.R.id.list);
		displayList.setEmptyView(empty);

		// Test list
		mTabIndex = (LinearLayout) v.findViewById(R.id.id_index);

		// Health survey
		mTabHealth = (LinearLayout) v.findViewById(R.id.id_class);
		mTabHealth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), HealthActivity.class);
				startActivityForResult(i, 0);
			}
		});
		// User survey
		mTabFind = (LinearLayout) v.findViewById(R.id.id_find);
		mTabFind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), InfoActivity.class);
				startActivityForResult(i, 0);
			}
		});

		mHintView1 = (ScrollView) v.findViewById(edu.osu.siyang.smartform.R.id.hint_view1);
		mHintView1.setVisibility(View.INVISIBLE);

		mHintView2 = (ScrollView) v.findViewById(edu.osu.siyang.smartform.R.id.hint_view2);
		mHintView2.setVisibility(View.INVISIBLE);

		mAboutUs1 = (ScrollView) v.findViewById(edu.osu.siyang.smartform.R.id.about_view1);
		mAboutUs1.setVisibility(View.INVISIBLE);

		mAboutUs2 = (ScrollView) v.findViewById(edu.osu.siyang.smartform.R.id.about_view2);
		mAboutUs2.setVisibility(View.INVISIBLE);

		mCopyright1 = (LinearLayout) v.findViewById(edu.osu.siyang.smartform.R.id.copyright_view1);
		mCopyright1.setVisibility(View.INVISIBLE);

		mCopyright2 = (LinearLayout) v.findViewById(edu.osu.siyang.smartform.R.id.copyright_view2);
		mCopyright2.setVisibility(View.INVISIBLE);

		textId1 = (TextView) v.findViewById(edu.osu.siyang.smartform.R.id.text_id1);
		textId1.setText(uid);
		textId2 = (TextView) v.findViewById(edu.osu.siyang.smartform.R.id.text_id2);
		textId2.setText(uid);

		copyId1 = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.copy_id1);
		copyId2 = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.copy_id2);
		OnClickListener copy = new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText(null, uid);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(getContext(), "User ID is copyed to clipboard!", Toast.LENGTH_SHORT).show();
			}
		};
		copyId1.setOnClickListener(copy);
		copyId2.setOnClickListener(copy);

		mAddTestButton = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.add_test_button);
		mAddTestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Test test = new Test();
				TestLab.get(getActivity()).addTest(test);
				copyId1.performClick();
				Intent i = new Intent(getActivity(), TestPagerActivity.class);
				i.putExtra(TestFragment.EXTRA_TEST_ID, test.getId());
				startActivityForResult(i, 0);
			}
		});

		// Link Button for Empty Display
		mNewTestButton = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.new_test_button);

		//  Initialize SharedPreferences
		mPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		//  Create a new boolean and preference and set it to true
		final boolean isFirstTour = mPref.getBoolean("firstTour", true);

		/* setup enter and exit animation */
		Animation enterAnimation = new AlphaAnimation(0f, 1f);
		enterAnimation.setDuration(600);
		enterAnimation.setFillAfter(true);

		Animation exitAnimation = new AlphaAnimation(1f, 0f);
		exitAnimation.setDuration(600);
		exitAnimation.setFillAfter(true);

		if(isFirstTour) {
			mTutorialHandler = TourGuide.init(this.getActivity()).with(TourGuide.Technique.Click)
					.setPointer(new Pointer())
					.setToolTip(new ToolTip().setTitle("Welcome!").setDescription("Click on Start Test to begin"))
					.setOverlay(new Overlay()
							.setEnterAnimation(enterAnimation)
							.setExitAnimation(exitAnimation))
					.playOn(mNewTestButton);
		}

		mNewTestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isFirstTour) {
					mTutorialHandler.cleanUp();
				}
				Test test = new Test();
				TestLab.get(getActivity()).addTest(test);

				Intent i = new Intent(getActivity(), TestPagerActivity.class);
				i.putExtra(TestFragment.EXTRA_TEST_ID, test.getId());
				startActivityForResult(i, 0);
			}
		});

		// Link Button for survey
		mHealthSurveyButton = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.health_survey_button);
		mHealthSurveyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://osu.az1.qualtrics.com/jfe/form/SV_eIPLorec5u70O3z"));
				startActivity(browserIntent);
			}
		});

		mUserSurveyButton = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.user_survey_button);
		mUserSurveyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://osu.az1.qualtrics.com/jfe/form/SV_1YsK1f5dZVByyxL"));
				startActivity(browserIntent);
			}
		});

		//Hide survey buttons
		//mUserSurveyButton.setVisibility(View.INVISIBLE);
		//mHealthSurveyButton.setVisibility(View.INVISIBLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if(getActivity().getActionBar()!=null) {
				//getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}

		// Register the ListView for a context menu
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			// Floating context menu for Froyo & Gingerbread
			registerForContextMenu(displayList);
		}
		else {
			// Use contextual action bar on Honeycomb and higher
			displayList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			displayList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// Required, but not used here
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					// Required, but not used here
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(edu.osu.siyang.smartform.R.menu.test_list_item_context, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch ( item.getItemId() ){
						case edu.osu.siyang.smartform.R.id.menu_item_delete_test:
							TestAdapter adapter = (TestAdapter) getListAdapter();
							TestLab testLab = TestLab.get(getActivity());

							for (int i = adapter.getCount() - 1; i >= 0; i--){
								if( getListView().isItemChecked(i)){
									testLab.deleteTest(adapter.getItem(i));
								}
							}

							mode.finish();
							adapter.notifyDataSetChanged();
							return true;
					}
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position,
													  long id, boolean checked) {
					// Required, but not used here
				}
			});
		}

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		((TestAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		TestLab.get(getActivity()).saveTests();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(edu.osu.siyang.smartform.R.menu.fragment_test_list, menu);
		MenuItem showSubtitle = menu.findItem(edu.osu.siyang.smartform.R.id.menu_item_show_hint);
		showSubtitle.setTitle(edu.osu.siyang.smartform.R.string.show_subtitle);
	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case edu.osu.siyang.smartform.R.id.menu_item_about_us:
				//Toggle
				if(mAboutVisible) {
					item.setTitle(edu.osu.siyang.smartform.R.string.hide_subtitle);
					mAboutUs1.setVisibility(View.VISIBLE);
					mAboutUs2.setVisibility(View.VISIBLE);
					mCopyright1.setVisibility(View.GONE);
					mCopyright2.setVisibility(View.GONE);
					mHintView1.setVisibility(View.GONE);
					mHintView2.setVisibility(View.GONE);
					mAboutVisible=false;
				} else {
					item.setTitle(edu.osu.siyang.smartform.R.string.about);
					mAboutUs1.setVisibility(View.GONE);
					mAboutUs2.setVisibility(View.GONE);
					mAboutVisible=true;
				}
				return true;
			case edu.osu.siyang.smartform.R.id.menu_item_show_info:
				//Toggle
				if(mInfoVisible) {
					item.setTitle(edu.osu.siyang.smartform.R.string.hide_subtitle);
					mCopyright1.setVisibility(View.VISIBLE);
					mCopyright2.setVisibility(View.VISIBLE);
					mAboutUs1.setVisibility(View.GONE);
					mAboutUs2.setVisibility(View.GONE);
					mHintView1.setVisibility(View.GONE);
					mHintView2.setVisibility(View.GONE);
					mInfoVisible=false;
				} else {
					item.setTitle(edu.osu.siyang.smartform.R.string.info);
					mCopyright1.setVisibility(View.GONE);
					mCopyright2.setVisibility(View.GONE);
					mInfoVisible=true;
				}
				return true;
			case edu.osu.siyang.smartform.R.id.menu_item_show_hint:
				//Toggle
				if (mHintVisible) {
					item.setTitle(edu.osu.siyang.smartform.R.string.hide_subtitle);
					mHintView1.setVisibility(View.VISIBLE);
					mHintView2.setVisibility(View.VISIBLE);
					mAboutUs1.setVisibility(View.GONE);
					mAboutUs2.setVisibility(View.GONE);
					mCopyright1.setVisibility(View.GONE);
					mCopyright2.setVisibility(View.GONE);
					mHintVisible=false;
				} else {
					item.setTitle(edu.osu.siyang.smartform.R.string.show_subtitle);
					mHintView1.setVisibility(View.GONE);
					mHintView2.setVisibility(View.GONE);
					mHintVisible=true;
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		getActivity().getMenuInflater().inflate(edu.osu.siyang.smartform.R.menu.test_list_item_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		TestAdapter adapter = (TestAdapter)getListAdapter();
		Test test = adapter.getItem(position);

		switch(item.getItemId()){
			case edu.osu.siyang.smartform.R.id.menu_item_delete_test:
				TestLab.get(getActivity()).deleteTest(test);
				adapter.notifyDataSetChanged();
				return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Test c = ((TestAdapter) getListAdapter()).getItem(position);
		Log.d(TAG, c.getTitle() + " was clicked.");

		mCallbacks.onTestSelected(c);
	}
	
	public void updateUI(){
		((TestAdapter)getListAdapter()).notifyDataSetChanged();
	}

	private class TestAdapter extends ArrayAdapter<Test> {

		public TestAdapter(ArrayList<Test> tests) {
			super(getActivity(), 0, tests);
		}

		// Overriding this method is what allows us to use the custom list. This
		// is what gets called
		// behind the scenes when ListView and adapter have their conversations
		// about what to display
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// If we weren't given a view, then inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						edu.osu.siyang.smartform.R.layout.list_item_test, null);
			}

			// Configure the view for this specific Test
			Test c = getItem(position);

			TextView titleTextView = (TextView) convertView
					.findViewById(edu.osu.siyang.smartform.R.id.test_list_item_titleTextView);
			titleTextView.setText(c.getTitle());

			TextView dateTextView = (TextView) convertView
					.findViewById(edu.osu.siyang.smartform.R.id.test_list_item_dateTextView);
			String formatDate = DateFormat.format("yyyy-MM-dd hh:mm:ss a",
					c.getDate()).toString();
			dateTextView.setText(formatDate);

			return convertView;
		}

	}

}
