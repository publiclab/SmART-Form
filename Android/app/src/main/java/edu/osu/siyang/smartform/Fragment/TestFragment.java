package edu.osu.siyang.smartform.Fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationManagerCompat;
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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import edu.osu.siyang.smartform.Activity.AboutFormalActivity;
import edu.osu.siyang.smartform.Activity.DataActivity;
import edu.osu.siyang.smartform.Activity.CameraActivity;
import edu.osu.siyang.smartform.Activity.DataActivity;
import edu.osu.siyang.smartform.Activity.HealthActivity;
import edu.osu.siyang.smartform.Activity.TestListActivity;
import edu.osu.siyang.smartform.Activity.TimerService;
import edu.osu.siyang.smartform.Bean.Photo;
import edu.osu.siyang.smartform.Bean.Test;
import edu.osu.siyang.smartform.Bean.TestLab;
import edu.osu.siyang.smartform.R;
import edu.osu.siyang.smartform.Util.MyReceiver;
import edu.osu.siyang.smartform.Util.NotificationPublisher;
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
	private Button mHealthButton;
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
	private MyCount counter;

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
		mTempSpinner.setSelection(1);

		mHumdSpinner = (Spinner) v.findViewById(R.id.spinner2);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(),
				R.array.humd_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mHumdSpinner.setAdapter(adapter2);
		mHumdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				String selectedItem = parent.getItemAtPosition(position).toString();
				if(selectedItem.equals(">80% RH"))
				{
					// do your stuff
					AlertDialog diaBox = HighHumidity();
					diaBox.show();				}
			} // to close the onItemSelected
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});


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
		//final boolean isFirstTour = mPref.getBoolean("firstTour", true);

		/* setup enter and exit animation */
		Animation enterAnimation = new AlphaAnimation(0f, 1f);
		enterAnimation.setDuration(600);
		enterAnimation.setFillAfter(true);

		Animation exitAnimation = new AlphaAnimation(1f, 0f);
		exitAnimation.setDuration(600);
		exitAnimation.setFillAfter(true);

		if(false) {
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

				/*
				if(isFirstTour) {
					mTutorialHandler.cleanUp();
					mTutorialHandler.setToolTip(new ToolTip().setTitle("Before button").setDescription("Click to take the before exposure image").setGravity(Gravity.TOP)).playOn(mBeforeButton);
				}*/
			}
		});

		// Date Button
		mDateButton = (TextView) v.findViewById(edu.osu.siyang.smartform.R.id.test_date);
		if (mTest.getDate() == null) mTest.setDate(new Date());
		mDateButton.setText(new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(mTest.getDate()));


		// Time Button
		mTimeButton = (TextView) v.findViewById(edu.osu.siyang.smartform.R.id.test_date);


		// Before/After TextView
		mBeforeText = (TextView) v.findViewById(R.id.before_textView);
		mAfterText = (TextView) v.findViewById(R.id.after_textView);

		ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
		File directory = cw.getDir("SmartForm", Context.MODE_PRIVATE);
		String path = directory.getAbsolutePath();

		// Before Button
		mBeforeButton = (ImageView) v.findViewById(R.id.before_bitmapBtn);
		if(mTest.getBefore() != null) {
			Bitmap bmp = null;
			try {
				File f=new File(path, mTest.getBefore());
				bmp = BitmapFactory.decodeStream(new FileInputStream(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Drawable d = new BitmapDrawable(getResources(), bmp);
			mBeforeText.setVisibility(View.INVISIBLE);
			mBeforeButton.setImageDrawable(d);
		}
		mBeforeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				if(isFirstTour) {
					mTutorialHandler.cleanUp();
					mTutorialHandler.setToolTip(new ToolTip().setTitle("After button").setDescription("Click to take the after exposure image").setGravity(Gravity.TOP)).playOn(mAfterButton);
				}*/

				//mTimeButton.performClick();
				Intent i = new Intent(getActivity(), CameraActivity.class);
				i.putExtra("TEST_PARAM", mTest.getTitle());
				i.putExtra("TEST_TAG", "before");
				startActivityForResult(i, REQUEST_BEFORE);


			}
		});

		// After Button
		mAfterButton = (ImageView) v.findViewById(edu.osu.siyang.smartform.R.id.after_bitmapBtn);
		if(mTest.getAfter() != null) {
			Bitmap bmp = null;
			try {
				File f=new File(path, mTest.getAfter());
				bmp = BitmapFactory.decodeStream(new FileInputStream(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Drawable d = new BitmapDrawable(getResources(), bmp);
			mAfterText.setVisibility(View.INVISIBLE);
			mAfterButton.setImageDrawable(d);
		}
		mAfterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				if(isFirstTour) {
					mTutorialHandler.cleanUp();
					mTutorialHandler.setToolTip(new ToolTip().setTitle("Upload button").setDescription("Click to upload data and finish").setGravity(Gravity.TOP)).playOn(mUploadButton);
				}*/

				Intent i = new Intent(getActivity(), CameraActivity.class);
				i.putExtra("TEST_PARAM", mTest.getTitle());
				i.putExtra("TEST_TAG", "after");
				startActivityForResult(i, REQUEST_AFTER);


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

		// Test Report
		mUploadButton = (Button) v.findViewById(edu.osu.siyang.smartform.R.id.test_uploadButton);
		mUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//mTest.setState(3);
				mCallbacks.onTestUpdated(mTest);
				Intent browserIntent = new Intent(getActivity(), DataActivity.class);
				startActivity(browserIntent);

			}
		});

		mHealthButton = (Button) v.findViewById(R.id.test_healthButton);
		mHealthButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallbacks.onTestUpdated(mTest);
				Intent browserIntent = new Intent(getActivity(), HealthActivity.class);
				startActivity(browserIntent);
			}
		});

		showResult();

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
				break;
			case 1:
				mBeforeButton.setEnabled(true);
				mAfterButton.setEnabled(true);
				break;
			case 2:
				mBeforeButton.setEnabled(true);
				mAfterButton.setEnabled(true);
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
		if(counter != null) {
			counter.cancel();
		}
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

		else if ( requestCode == REQUEST_BEFORE) {
			String path = data.getStringExtra(CameraActivity.EXTRA_CAMERA_DATA);
			String fileName = data.getStringExtra(CameraActivity.EXTRA_FILE_NAME);

			Log.e(TAG, fileName);
			if ( fileName != null ) {
				File f = new File(path, fileName);
				mTest.setBefore(fileName);
				mTest.setState(1);
				long dtMili = System.currentTimeMillis();
				mTest.setStart(new Date(dtMili));
				mTest.setEnd(new Date(dtMili+3*24*60*60*1000L)); //72hours
				try {
					before = BitmapFactory.decodeStream(new FileInputStream(f));
					Drawable d = new BitmapDrawable(getResources(), before);
					mBeforeText.setVisibility(View.INVISIBLE);
					mBeforeButton.setImageDrawable(d);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Double ratio = getRatio(before);
				if(ratio>1) {
					AlertDialog diaBox = Contaminated();
					diaBox.show();
				} else {
					AlertDialog diaBox = NextStep();
					diaBox.show();
				}
				mCallbacks.onTestUpdated(mTest);
				scheduleNotification(getNotification("It's time to take the photo after 72 hours exposure!"),3*24*60*60*1000);
				showResult();

			}
		}

		else if ( requestCode == REQUEST_AFTER) {
			String path = data.getStringExtra(CameraActivity.EXTRA_CAMERA_DATA);
			String fileName = data.getStringExtra(CameraActivity.EXTRA_FILE_NAME);

			Log.e(TAG, fileName);
			if ( fileName != null ) {
				File f = new File(path, fileName);
				mTest.setAfter(fileName);
				mTest.setState(2);
				try {
					after = BitmapFactory.decodeStream(new FileInputStream(f));
					Drawable d = new BitmapDrawable(getResources(), after);
					mAfterText.setVisibility(View.INVISIBLE);
					mAfterButton.setImageDrawable(d);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Double rppb = getReading(after);
				Double hour = getHour();

				if(hour<12) {
					mTest.setResult("Invalid");
				} else if(rppb>120) {
					mTest.setResult(">120");
				} else if(rppb<0) {
					mTest.setResult("0");
				} else{
					mTest.setResult(Integer.toString(rppb.intValue()));
				}
				mCallbacks.onTestUpdated(mTest);
				showResult();

				if(hour < 12) {
					AlertDialog diaBox = ShortExposure();
					diaBox.show();
				} else if(rppb*hour < 72*20) {
					AlertDialog diaBox = LowReading();
					diaBox.show();
				} else if(rppb*hour > 72*120) {
					AlertDialog diaBox = HighReading();
					diaBox.show();
				}
			}
		}
	}


	private void scheduleNotification(Notification notification, int delay) {

		Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
		notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		long futureInMillis = SystemClock.elapsedRealtime() + delay;
		AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
	}

	private Notification getNotification(String content) {
		Notification.Builder builder = new Notification.Builder(getActivity());
		builder.setContentTitle("Scheduled Notification");
		builder.setContentText(content);
		builder.setSmallIcon(R.drawable.ic_launcher);
		return builder.build();
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

	private AlertDialog LowReading()
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

	private AlertDialog HighReading()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Your result is above the detection limit")
				.setMessage("Your formaldehyde concentration is elevated and has saturated the badge. You can retest with a new badge and take an image after 24 hours for more accurate results.")
				//.setIcon(R.drawable.delete)

				.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}
	private AlertDialog HighHumidity()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Is the relative humidity high?")
				.setMessage("The badge is unstable under high humidity (>80%). It's recommended you retake the photo with lower humidity for a better result.")
				//.setIcon(R.drawable.delete)

				.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}

	private AlertDialog ShortExposure()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Your exposure time is below the detection limit")
				.setMessage("Your badge exposure time is less than 12 hours, wait 72 hours prior to take the after picture.")
				//.setIcon(R.drawable.delete)

				.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}

	private AlertDialog NextStep()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Waiting for the next step?")
				.setMessage("You can take the health survey now, it can be found on bottom of this page.")
				//.setIcon(R.drawable.delete)

				.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}

	private AlertDialog Contaminated()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Is your badge contaminated?")
				.setMessage("Check you badge to see if it is already exposed. If so, use another badge.")
				//.setIcon(R.drawable.delete)

				.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}

	private AlertDialog Bluish()
	{
		@SuppressLint("RestrictedApi") AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), edu.osu.siyang.smartform.R.style.myDialog))
				//set message, title, and icon
				.setTitle("Is your badge bluish?")
				.setMessage("Relative humidity above 80% can cause incorrect results. Use a new badge in an area of low relative humidity.")
				//.setIcon(R.drawable.delete)

				.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

					}
				})
				.create();
		return myQuittingDialogBox;
	}

	private void showResult() {
		// TO DO: finish linear regression here
		Log.d(TAG, "Inside showResult: " + mTest.getState());
		mResultField.setText("");
		String res = mTest.getResult();
		String output = "";
		if(mTest.getState()==1) {
			long endTime = mTest.getEnd().getTime();
			long nowTime = System.currentTimeMillis();
			counter = new MyCount(endTime-nowTime,1000);
			counter.start();
			Log.d(TAG, "timer start");
		}
		if(mTest.getState()==2) {
			if(counter!=null) {
				counter.cancel();
				Log.d(TAG, "timer canceled");
			}
			output = res + " ppb";
			mResultField.setText(output);
		}
	}

	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub

			mResultField.setText("Time up!");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mResultField.setText(timeCalculate(millisUntilFinished/1000));

		}
	}

	public String timeCalculate(long ttime)
	{
		long  daysuuu,hoursuuu, minutesuuu, secondsuuu;
		String timeT = "";



		daysuuu = (Math.round(ttime) / 86400);
		hoursuuu = (Math.round(ttime) / 3600) - (daysuuu * 24);
		minutesuuu = (Math.round(ttime) / 60) - (daysuuu * 1440) - (hoursuuu * 60);
		secondsuuu = Math.round(ttime) % 60;

		timeT = daysuuu + "d " + hoursuuu + "h " + minutesuuu + "m " + secondsuuu + "s ";

		return timeT;
	}

	/**
	 * Linear regression model
	 * @param after
	 * @return
	 */
	private double getReading(Bitmap after) {
		double ratio = getRatio(after);
		double result = (-36301*ratio + 36671)/getHour();
		return result;
	}

	/**
	 * Calculate past hours
	 * @return
	 */
	private double getHour() {
		long timeNow = System.currentTimeMillis();
		long timeStart = mTest.getStart().getTime();
		double hour = (timeNow - timeStart)/(1000*60*60);
		Log.d(TAG, Double.toString((timeNow - timeStart)/1000));
		return hour;
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

	/**
	 * Resize drawable image
	 * @param image
	 * @return
	 */
	private Drawable resize(Drawable image) {
		Bitmap b = ((BitmapDrawable)image).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
		return new BitmapDrawable(getResources(), bitmapResized);
	}
}