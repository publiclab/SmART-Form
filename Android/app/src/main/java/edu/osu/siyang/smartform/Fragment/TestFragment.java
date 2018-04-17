package edu.osu.siyang.smartform.Fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import edu.osu.siyang.smartform.Activity.CameraActivity;
import edu.osu.siyang.smartform.Activity.TimerService;
import edu.osu.siyang.smartform.Bean.Photo;
import edu.osu.siyang.smartform.Bean.Test;
import edu.osu.siyang.smartform.Bean.TestLab;
import edu.osu.siyang.smartform.R;
import edu.osu.siyang.smartform.Util.PictureUtils;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import static android.graphics.Color.CYAN;

public class TestFragment extends DialogFragment {

	public TourGuide mTutorialHandler;
	public static BufferedWriter out;

	public static final String EXTRA_TEST_ID = "com.osu.siyang.smartform.test_id";
	private static final String DIALOG_DATE = "date";
	private static final int REQUEST_DATE = 0;

	private static final String DIALOG_TIME = "time";
	private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_TIME = 1;
	private static final int REQUEST_PHOTO = 2;
	private static final int REQUEST_BEFORE = 3;
	private static final int REQUEST_AFTER = 4;
	private static final String TAG_KIO = "KIO";
	private static final String TAG = "TestFragment";
	private int mCounter = 0;
	private int mTimer = 0;
	private Test mTest;
	private EditText mTitleField;
	private TextView mResultField;
	private TextView mDateButton;
	private TextView mTimeButton;
	private TextView mBeforeText;
	private TextView mAfterText;
	private ImageView mBeforeButton;
	private ImageView mAfterButton;
	private Spinner mTempSpinner;
	private Spinner mHumdSpinner;
	private Button mUploadButton;
	private Callbacks mCallbacks;
	private Bitmap before;
	private Bitmap after;
	private int int_hours = 72;
	private String date_time;
	private Calendar calendar;
	private SimpleDateFormat simpleDateFormat;
	private SharedPreferences mPref;
	private SharedPreferences.Editor mEditor;
	private InputMethodManager imm;

	/**
	 * Required interface for hosting activities
	 */
	public interface Callbacks{
		void onTestUpdated(Test test);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach(){
		super.onDetach();
		mCallbacks=null;
	}

	public static TestFragment newInstance(UUID testId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TEST_ID, testId);

		TestFragment fragment = new TestFragment();
		fragment.setArguments(args);

		return fragment;
	}

	private void updateDate() {
		final Handler someHandler = new Handler();
		someHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mDateButton.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US).format(new Date()));
				someHandler.postDelayed(this, 1000);
			}
		}, 10);
	}

	private void updateTime() {
		// TODO KIO Is this really necessary to creat a calendar?
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(mTest.getDate());

		Log.d(TAG_KIO, "Inside updateTime() mDate time is: "
				+ mTest.getDate().getTime());
		Log.d(TAG_KIO, "Inside updateTime() mDate is: " + mTest.getDate());

		Time time = new Time();
		time.set(mTest.getDate().getTime());

		String timeFormat = time.format("%I:%M");
		Log.d(TAG_KIO, "timeFormat is: " + timeFormat);
		mTimeButton.setText(timeFormat);
	}

	@Override
	public void onStart() {
		super.onStart();
		//showPhoto();
		showResult();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		// Get test ID from host Activity (which was passed it from calling
		// activity TestList
		UUID testId = (UUID) getArguments().getSerializable(EXTRA_TEST_ID);
		mTest = TestLab.get(getActivity()).getTest(testId);
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
							 Bundle savedInstance) {

		// Get view for fragment
		View v = inflater.inflate(edu.osu.siyang.smartform.R.layout.fragment_test, parent, false);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if(NavUtils.getParentActivityName(getActivity()) != null){
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

		// Temp/Humd spinners
		mTempSpinner = (Spinner) v.findViewById(R.id.spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
				R.array.temp_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mTempSpinner.setAdapter(adapter);

		mHumdSpinner = (Spinner) v.findViewById(R.id.spinner2);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(),
				R.array.humd_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mTempSpinner.setAdapter(adapter2);


		// Result
		mResultField = (TextView) v.findViewById(edu.osu.siyang.smartform.R.id.test_result);

		// Test Title
		mTitleField = (EditText) v.findViewById(edu.osu.siyang.smartform.R.id.test_title);
		mTitleField.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					mTitleField.setFocusableInTouchMode(false);
					mTitleField.setFocusable(false);
					imm.hideSoftInputFromWindow(mTitleField.getWindowToken(), 0);

					mCounter=0;

					return true;
				}
				return false;
			}
		});

		mTitleField.setFocusableInTouchMode(false);
		mTitleField.setFocusable(false);
		mTitleField.setText(mTest.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before,
									  int count) {
				mTest.setTitle(c.toString());
				mCallbacks.onTestUpdated(mTest);
			}

			public void beforeTextChanged(CharSequence c, int start, int count,
										  int after) {
				// Do something?
			}

			public void afterTextChanged(Editable c) {
				// Do something?
			}
		});
		Log.d("KIO", "Test title is: " + mTitleField);

		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		// Edit title
		//mTitleEdit = (ImageButton) v.findViewById(edu.osu.siyang.smartform.R.id.title_edit);

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
					.setToolTip(new ToolTip().setTitle("Test detail").setDescription("Click to edit parameters"))
					.setOverlay(new Overlay()
							.setEnterAnimation(enterAnimation)
							.setExitAnimation(exitAnimation))
					.playOn(mTitleField);

			try {
				createFileOnDevice(true);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		mTitleField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

						mTitleField.setFocusableInTouchMode(true);
						mTitleField.setFocusable(true);
						imm.showSoftInput(mTitleField, 0);


				if(isFirstTour) {
					mTutorialHandler.cleanUp();
					mTutorialHandler.setToolTip(new ToolTip().setTitle("Before button").setDescription("Click to take the before exposure image").setGravity(Gravity.TOP)).playOn(mBeforeButton);
				}
			}
		});

		// Date Button
		mDateButton = (TextView) v.findViewById(edu.osu.siyang.smartform.R.id.test_date);
		if (mTest.getDate() == null) mTest.setDate(new Date());
		updateDate();

		/*
		mDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment
						.newInstance(mTest.getDate());
				dialog.setTargetFragment(TestFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		Log.d("KIO", "Date is: " + mDateButton);
		*/


		// Time Button
		mTimeButton = (TextView) v.findViewById(edu.osu.siyang.smartform.R.id.test_date);
		mPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		mEditor = mPref.edit();
		String str_value = mPref.getString("data", "");
		mTimeButton.setText(date_time);
		mTimeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "click on timer");
				calendar = Calendar.getInstance();
				simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
				date_time = simpleDateFormat.format(calendar.getTime());

				mEditor.putString("data", date_time).commit();
				mEditor.putString("hours", "24").commit();

				Intent intent_service = new Intent(getActivity().getApplicationContext(), TimerService.class);
				getActivity().startService(intent_service);
				// mTimer = 1;
				// getActivity().startService(new Intent(getActivity(), TimerService.class));
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment
						.newInstance(mTest.getDate());
				dialog.setTargetFragment(TestFragment.this, REQUEST_TIME);
				dialog.show(fm, DIALOG_TIME);
			}
		});


		// "finished" Check box
		/*
		mFinishedCheckBox = (CheckBox) v.findViewById(edu.osu.siyang.smartform.R.id.test_finished);
		mFinishedCheckBox.setChecked(mTest.isFinished());
		mFinishedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
												 boolean isChecked) {
						// Set the test's finished property
						mTest.setFinished(isChecked);
						mCallbacks.onTestUpdated(mTest);
					}
				});
		*/
		// Photo Button
		/*
		mPhotoButton = (ImageButton) v.findViewById(edu.osu.siyang.smartform.R.id.test_imageButton);
		mPhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), TestCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});
		*/

		// Before/After TextView
		mBeforeText = (TextView) v.findViewById(R.id.before_textView);
		mAfterText = (TextView) v.findViewById(R.id.after_textView);

		// Before Button
		mBeforeButton = (ImageView) v.findViewById(R.id.before_bitmapBtn);
		mBeforeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//mTimeButton.performClick();
				Intent i = new Intent(getActivity(), CameraActivity.class);
				i.putExtra("TEST_PARAM", mTest.getTitle());
				i.putExtra("TEST_TAG", "before");
				startActivityForResult(i, REQUEST_BEFORE);

				if(isFirstTour) {
					mTutorialHandler.cleanUp();
					mTutorialHandler.setToolTip(new ToolTip().setTitle("After button").setDescription("Click to take the after exposure image").setGravity(Gravity.TOP)).playOn(mAfterButton);
				}
			}
		});

		// After Button
		mAfterButton = (ImageView) v.findViewById(edu.osu.siyang.smartform.R.id.after_bitmapBtn);
		//mAfterButton.setEnabled(false);
		mAfterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CameraActivity.class);
				i.putExtra("TEST_PARAM", mTest.getTitle());
				i.putExtra("TEST_TAG", "after");
				startActivityForResult(i, REQUEST_AFTER);

				if(isFirstTour) {
					mTutorialHandler.cleanUp();
					mTutorialHandler.setToolTip(new ToolTip().setTitle("Upload button").setDescription("Click to upload data and finish").setGravity(Gravity.TOP)).playOn(mUploadButton);
				}

				// Write to log file
				/*
				writeToFile("Date: " + mTest.getDate());
				writeToFile("ID: " + mTest.getId());
				writeToFile("Title: " + mTest.getTitle());
				writeToFile("Result: " + mTest.getResult());
				writeToFile("Before: " + mTest.getBefore());
				writeToFile("After: " + mTest.getAfter());
				*/
			}
		});


		// If a camera is not available, disable the camera functionality
		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
				pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
				(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
						Camera.getNumberOfCameras() > 0 );

		if (!hasACamera) {
			mBeforeButton.setEnabled(false);
			mAfterButton.setEnabled(false);
		}

		// Photographic Evidence
		/*
		mPhotoView = (ImageView) v.findViewById(edu.osu.siyang.smartform.R.id.test_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Photo p = mTest.getPhoto();
				if ( p == null ){
					return;
				}

				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
			}
		});
		*/

		// Test Report
		mUploadButton = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.test_uploadButton);
		mUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isFirstTour) {
					mTutorialHandler.cleanUp();
					//  Make a new preferences editor
					mEditor = mPref.edit();

					//  Edit preference to make it false because we don't want this to run again
					mEditor.putBoolean("firstTour", false);

					//  Apply changes
					mEditor.apply();
				}

				//new AppEULA(getActivity()).show();
				//mFinishedCheckBox.setChecked(true);
				mTest.setFinished(true);
				mTest.setState(3);
				mCallbacks.onTestUpdated(mTest);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://osu.az1.qualtrics.com/jfe/form/SV_5u9FnmAiYtRQtp3"));
				startActivity(browserIntent);
			}
		});

		// About formaldehyde
		/*
		mAboutButton = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.test_aboutButton);
		mAboutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().setContentView(edu.osu.siyang.smartform.R.layout.fragment_aboutformaldehyde);
			}
		});
		*/
		// Hide about button
		//mAboutButton.setVisibility(View.INVISIBLE);

		return v;
	}

	private void createFileOnDevice(Boolean append) throws IOException {
                /*
                 * Function to initially create the log file and it also writes the time of creation to file.
                 */
		File Root = Environment.getExternalStorageDirectory();
		if(Root.canWrite()){
			File  LogFile = new File(Root, "Log.txt");
			FileWriter LogWriter = new FileWriter(LogFile, append);
			out = new BufferedWriter(LogWriter);
			Date date = new Date();
			out.write("Logged at" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n"));
			out.close();

		}
	}

	public void writeToFile(String message) {
		try {
			out.write(message + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String str_time = intent.getStringExtra("time");
			mTimeButton.setText(str_time);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// Current state
		switch (mTest.getState()) {
			case 0:
				mBeforeButton.setEnabled(true);
				mAfterButton.setEnabled(false);
				mUploadButton.setEnabled(false);
				break;
			case 1:
				mBeforeButton.setEnabled(true);
				mAfterButton.setEnabled(true);
				mUploadButton.setEnabled(false);
				break;
			case 2:
				mBeforeButton.setEnabled(true);
				mAfterButton.setEnabled(true);
				mUploadButton.setEnabled(true);
				break;
		}
		getContext().registerReceiver(broadcastReceiver,new IntentFilter(TimerService.str_receiver));
	}

	@Override
	public void onPause() {
		super.onPause();
		TestLab.get(getActivity()).saveTests();
		getContext().unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onStop() {
		super.onStop();
		//PictureUtils.cleanImageView(mPhotoView);
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getActivity().getCurrentFocus();
			if ( v instanceof EditText) {
				Rect outRect = new Rect();
				v.getGlobalVisibleRect(outRect);
				if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
					v.clearFocus();
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		}
		return super.getActivity().dispatchTouchEvent( event );
	}

    /**
     * Define requests in activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		else if (requestCode == REQUEST_DATE) {
			Date date = (Date) data
					.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mTest.setDate(date);
			mCallbacks.onTestUpdated(mTest);
			updateDate();
		}

		else if (requestCode == REQUEST_TIME) {
			Date date = (Date) data
					.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			mTest.setDate(date);
			updateTime();
		}

		else if ( requestCode == REQUEST_PHOTO) {
			// Create a new Photo object and attach it to the test
			String filename = data.getStringExtra(TestCameraFragment.EXTRA_PHOTO_FILENAME);

			if ( filename != null ){
				Photo p = new Photo(filename);
				mTest.setPhoto(p);
				mCallbacks.onTestUpdated(mTest);
				showPhoto();
			}
		}

		else if ( requestCode == REQUEST_BEFORE) {
			String bitmap = data.getStringExtra(CameraActivity.EXTRA_CAMERA_DATA);
			Drawable d = new BitmapDrawable(getResources(), bitmap);
			mBeforeText.setVisibility(View.INVISIBLE);
			mBeforeButton.setImageDrawable(d);
			Log.e(TAG, bitmap);
			if ( bitmap != null ) {
				Uri uri = Uri.parse(bitmap);
				mTest.setBefore(uri);
				mTest.setState(1);
				try {
					before = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCallbacks.onTestUpdated(mTest);
			}
		}

		else if ( requestCode == REQUEST_AFTER) {
			String bitmap = data.getStringExtra(CameraActivity.EXTRA_CAMERA_DATA);
			Drawable d = new BitmapDrawable(getResources(), bitmap);
			mAfterText.setVisibility(View.INVISIBLE);
			mAfterButton.setImageDrawable(d);
			Log.e(TAG, bitmap);
			if ( bitmap != null ) {
				Uri uri = Uri.parse(bitmap);
				mTest.setAfter(uri);
				mTest.setState(2);
				try {
					after = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
				} catch (IOException e) {
					e.printStackTrace();
				}
				int res = getResult(before,after);
				if(res!=0) mTest.setResult(res);
				if(res<20) {
					AlertDialog diaBox = RetakeAfter();
					diaBox.show();
				}
				mCallbacks.onTestUpdated(mTest);
				showResult();
			}
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(edu.osu.siyang.smartform.R.menu.fragment_test, menu);
	}

	/**
	 * Customize option bar
	 * @param item
	 * @return
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ){
			case android.R.id.home:
				if ( NavUtils.getParentActivityName(getActivity()) != null){
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case edu.osu.siyang.smartform.R.id.menu_item_delete_single_test:
				AlertDialog diaBox = AskOption();
				diaBox.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private AlertDialog AskOption()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Delete")
				.setMessage("Are you sure about delete this test?")
				//.setIcon(R.drawable.delete)

				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						//your deleting code
						TestLab.get(getActivity()).deleteTest(mTest);
						if ( NavUtils.getParentActivityName(getActivity()) != null){
							NavUtils.navigateUpFromSameTask(getActivity());
						}
						dialog.dismiss();
					}

				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}

	private AlertDialog RetakeAfter()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Your result is below the detection limit")
				.setMessage("Your formaldehyde concentration is low (<20ppb). For a more accurate result you can optionally expose the badge for another four days and retake the photo.")
				//.setIcon(R.drawable.delete)

				.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}

	private void showPhoto() {
		// (Re)set the image button's image based on our photo
		Photo p = mTest.getPhoto();
		BitmapDrawable b = null;

		Log.d(TAG, "Inside showPhoto");

		if ( p != null ){
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		//mPhotoView.setImageDrawable(b);
	}

	private void showResult() {
		// TO DO: finish linear regression here
		Log.d(TAG, "Inside showResult");
		int res = mTest.getResult();
		if(res == -1)
			mResultField.setText(("0 pbb"));
		else if(res != 0)
			mResultField.setText(Integer.toString(res) + " ppb");
	}


	/**
	 * Linear regression model
	 * @param before
	 * @param after
	 * @return
	 */
	private int getResult(Bitmap before, Bitmap after) {
		double b = 0;
		double a = 0;
		int result = 0;
		b = getRatio(before);
		a = getRatio(after);
		if(b > a) {
			result = (int) ((b - a) * 40 / 0.2433);
		} else {
			result = -1;
		}
		return result;
	}

	/**
	 * Color intensity in HSI
	 * @param bitmap
	 * @return
	 */
	private double getLightness(Bitmap bitmap) {
		int redColors = 0;
		int greenColors = 0;
		int blueColors = 0;
		int pixelCount = 0;
		for (int y = 0; y < bitmap.getHeight(); y++)
		{
			for (int x = 0; x < bitmap.getWidth(); x++)
			{
				int c = bitmap.getPixel(x, y);
				pixelCount++;
				redColors += Color.red(c);
				greenColors += Color.green(c);
				blueColors += Color.blue(c);
			}
		}
		// calculate average of bitmap r,g,b values
		double red = (redColors/pixelCount)/255.0;
		double green = (greenColors/pixelCount)/255.0;
		double blue = (blueColors/pixelCount)/255.0;
		double i = (red+green+blue)/3;
		//Log.d(TAG, "intensity= " + i);
		return i;
	}

	/**
	 * Calibration patch correction
	 * @param bitmap
	 * @return
	 */
	private double getRatio(Bitmap bitmap) {
		Bitmap ref, act;
		double ratio = 0;
		if(bitmap!=null) {
			act = Bitmap.createBitmap(bitmap, 50, 150, 50, 50);
			ref = Bitmap.createBitmap(bitmap, 150, 150, 50, 50);

			ratio = getLightness(act) / getLightness(ref); // Chemical badge intensity / calibrating patch intensity
		}
		return ratio;
	}

}