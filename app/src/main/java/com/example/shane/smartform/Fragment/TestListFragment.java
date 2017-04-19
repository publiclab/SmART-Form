package com.example.shane.smartform.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shane.smartform.Activity.AppEULA;
import com.example.shane.smartform.Activity.IntroActivity;
import com.example.shane.smartform.Activity.TestListActivity;
import com.example.shane.smartform.Activity.TestPagerActivity;
import com.example.shane.smartform.R;
import com.example.shane.smartform.Bean.Test;
import com.example.shane.smartform.Bean.TestLab;

import java.util.ArrayList;
import com.surveymonkey.surveymonkeyandroidsdk.SurveyMonkey;
import com.surveymonkey.surveymonkeyandroidsdk.utils.SMError;


public class TestListFragment extends ListFragment {

	private static final String TAG = "TestListFragment";

	private ArrayList<Test> mTests;
	private boolean mSubtitleVisible;
	private Callbacks mCallbacks;
	
	private Button mNewTestButton;
	private Button mAddTestButton;
	private FloatingActionButton floatingActionButton;

	/**
	 * Required interface for hosting activities
	 */
	public interface Callbacks {
		void onTestSelected(Test test);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		// unchecked cast, so you must document this somehwere!
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
		getActivity().setTitle(R.string.app_name);

		//  Declare a new thread to do a preference check
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				//  Initialize SharedPreferences
				SharedPreferences getPrefs = PreferenceManager
						.getDefaultSharedPreferences(getActivity().getBaseContext());

				//  Create a new boolean and preference and set it to true
				boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

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
		mSubtitleVisible = false;
	}
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		// Get view for fragment
		View v = inflater.inflate(R.layout.fragment_test_list, parent, false);
		
		// Set custom empty view
		View empty = v.findViewById(R.id.custom_empty_view);
		ListView displayList = (ListView) v.findViewById(android.R.id.list);
		displayList.setEmptyView(empty);

		// Set floatingActionButton for New Test
		/*
		floatingActionButton = (FloatingActionButton) v.findViewById(R.id.floating_btn);
		floatingActionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Test test = new Test();
				TestLab.get(getActivity()).addTest(test);

				((TestAdapter) getListAdapter()).notifyDataSetChanged();
				mCallbacks.onTestSelected(test);
			}
		});
		*/
		mAddTestButton = (Button) v.findViewById(R.id.add_test_button);
		mAddTestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Test test = new Test();
				TestLab.get(getActivity()).addTest(test);

				Intent i = new Intent(getActivity(), TestPagerActivity.class);
				i.putExtra(TestFragment.EXTRA_TEST_ID, test.getId());
				startActivityForResult(i, 0);
			}
		});

		// Link Button for Empty Display
		mNewTestButton = (Button) v.findViewById(R.id.new_test_button);
		mNewTestButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Test test = new Test();
				TestLab.get(getActivity()).addTest(test);

				Intent i = new Intent(getActivity(), TestPagerActivity.class);
				i.putExtra(TestFragment.EXTRA_TEST_ID, test.getId());
				startActivityForResult(i, 0);
			}
		});


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if(getActivity().getActionBar()!=null) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
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
					inflater.inflate(R.menu.test_list_item_context, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch ( item.getItemId() ){
						case R.id.menu_item_delete_test:
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
		inflater.inflate(R.menu.fragment_test_list, menu);
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleVisible && showSubtitle != null){
			showSubtitle.setTitle(R.string.show_subtitle);
		}
	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_test:
				Test test = new Test();
				TestLab.get(getActivity()).addTest(test);
				
				((TestAdapter) getListAdapter()).notifyDataSetChanged();
				mCallbacks.onTestSelected(test);
				return true;
			case R.id.menu_item_show_subtitle:
				if( getActivity().getActionBar().getSubtitle() == null ) {
					//getActivity().getActionBar().setSubtitle(R.string.subtitle);
					mSubtitleVisible = true;
					Toast.makeText(getActivity(), R.string.hints, Toast.LENGTH_LONG).show();
					item.setTitle(R.string.hide_subtitle);
				}
				else {
					getActivity().getActionBar().setSubtitle(null);
					mSubtitleVisible = false;
					item.setTitle(R.string.show_subtitle);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		getActivity().getMenuInflater().inflate(R.menu.test_list_item_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		TestAdapter adapter = (TestAdapter)getListAdapter();
		Test test = adapter.getItem(position);
		
		switch(item.getItemId()){
			case R.id.menu_item_delete_test:
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
						R.layout.list_item_test, null);
			}

			// Configure the view for this specific Test
			Test c = getItem(position);

			TextView titleTextView = (TextView) convertView
					.findViewById(R.id.test_list_item_titleTextView);
			titleTextView.setText(c.getTitle());

			TextView dateTextView = (TextView) convertView
					.findViewById(R.id.test_list_item_dateTextView);
			String formatDate = DateFormat.format("EEEE, MMM dd, yyyy",
					c.getDate()).toString();
			dateTextView.setText(formatDate);

			CheckBox solvedCheckBox = (CheckBox) convertView
					.findViewById(R.id.test_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.isFinished());

			return convertView;
		}

	}

}
