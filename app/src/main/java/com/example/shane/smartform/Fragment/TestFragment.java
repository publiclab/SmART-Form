package com.example.shane.smartform.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shane.smartform.Activity.CameraActivity;
import com.example.shane.smartform.Activity.TestCameraActivity;
import com.example.shane.smartform.Bean.Photo;
import com.example.shane.smartform.Util.PictureUtils;
import com.example.shane.smartform.R;
import com.example.shane.smartform.Bean.Test;
import com.example.shane.smartform.Bean.TestLab;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class TestFragment extends DialogFragment{

	public static final String EXTRA_TEST_ID = "com.example.shane.smartform.test_id";
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
	private Test mTest;
	private EditText mTitleField;
    private ImageButton mTitleEdit;
    private TextView mResultField;
	private Button mDateButton;
	private Button mTimeButton;
	private Button mBeforeButton;
	private Button mAfterButton;
	private CheckBox mFinishedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	private Callbacks mCallbacks;

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
		String formatDate = DateFormat.format("EEEE, MMM dd, yyyy",
				mTest.getDate()).toString();
		mDateButton.setText(formatDate);
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
		showPhoto();
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
		View v = inflater.inflate(R.layout.fragment_test, parent, false);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if(NavUtils.getParentActivityName(getActivity()) != null){
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

        // Result
        mResultField = (TextView) v.findViewById(R.id.test_result);

		// Test Title
		mTitleField = (EditText) v.findViewById(R.id.test_title);
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

        // Edit title
        mTitleEdit = (ImageButton) v.findViewById(R.id.title_edit);
        mTitleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Log.d(TAG, "mCounter = " + mCounter);
				switch (mCounter) {
					case 0:
						mTitleField.setFocusableInTouchMode(true);
						mTitleField.setFocusable(true);
						mCounter=1;
						break;
					case 1:
						mTitleField.setFocusable(false);
						mCounter=0;
						break;
				}
            }
        });

		// Date Button
		mDateButton = (Button) v.findViewById(R.id.test_date);
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
		mTimeButton = (Button) v.findViewById(R.id.test_time);
		Log.d("KIO", "Time is: " + mTimeButton);
		updateTime();
		/*
		mTimeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment
						.newInstance(mTest.getDate());
				dialog.setTargetFragment(TestFragment.this, REQUEST_TIME);
				dialog.show(fm, DIALOG_TIME);
			}
		});
		Log.d("KIO", "Time is: " + mTimeButton);
		*/

		// "finished" Check box
		mFinishedCheckBox = (CheckBox) v.findViewById(R.id.test_finished);
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

		// Photo Button
		mPhotoButton = (ImageButton) v.findViewById(R.id.test_imageButton);
		mPhotoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), TestCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});

        // Before Button
        mBeforeButton = (Button) v.findViewById(R.id.before_bitmapBtn);
        mBeforeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CameraActivity.class);
                startActivityForResult(i, REQUEST_BEFORE);
            }
        });

        // After Button
        mAfterButton = (Button) v.findViewById(R.id.after_bitmapBtn);
        mAfterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CameraActivity.class);
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
			mPhotoButton.setEnabled(false);
            mBeforeButton.setEnabled(false);
            mAfterButton.setEnabled(false);
		}
		
		// Photographic Evidence
		mPhotoView = (ImageView) v.findViewById(R.id.test_imageView);
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
		
		// Test Report
		Button reportButton = (Button) v.findViewById(R.id.test_reportButton);
		reportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getTestReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.test_report_subject));
				
				// Always show the chooser, even if the user has set a default app
				i = Intent.createChooser(i, getString(R.string.send_report));
				
				startActivity(i);
			}
		});
		
		return v;
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("KIO", "onPause");
		TestLab.get(getActivity()).saveTests();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
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
            Log.e(TAG, bitmap);
            if ( bitmap != null ) {
                Uri uri = Uri.parse(bitmap);
                mTest.setBefore(uri);
                mCallbacks.onTestUpdated(mTest);
            }
        }

        else if ( requestCode == REQUEST_AFTER) {
            String bitmap = data.getStringExtra(CameraActivity.EXTRA_CAMERA_DATA);
            Log.e(TAG, bitmap);
            if ( bitmap != null ) {
                Uri uri = Uri.parse(bitmap);
                mTest.setAfter(uri);
                mTest.setResult(0);
                mCallbacks.onTestUpdated(mTest);
                showResult();
            }
        }
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_test, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ){
			case android.R.id.home:
				if ( NavUtils.getParentActivityName(getActivity()) != null){
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			case R.id.menu_item_delete_single_test:
				AlertDialog diaBox = AskOption();
				diaBox.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private AlertDialog AskOption()
	{
		AlertDialog myQuittingDialogBox =new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
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

	private void showPhoto() {
		// (Re)set the image button's image based on our photo
		Photo p = mTest.getPhoto();
		BitmapDrawable b = null;
		
		Log.d(TAG, "Inside showPhoto");
		
		if ( p != null ){
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);
		}
		mPhotoView.setImageDrawable(b);
	}

    private void showResult() {
        // TO DO: finish linear regression here
        int res = mTest.getResult();
        Uri beforeUri = mTest.getBefore();
        Uri afterUri = mTest.getAfter();
        Log.d(TAG, "Inside showResult");

        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
        if (res != 0) {
            mResultField.setText(Integer.toString(res) + "ppm");
        }
    }

	private String getTestReport() {
		String finishedString = null;
		
		if( mTest.isFinished() ){
			finishedString = getString(R.string.test_report_finished);
		}
		else {
			finishedString = getString(R.string.test_report_unfinished);
		}
		
		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mTest.getDate()).toString();
		
		int result = mTest.getResult();

		String report = getString(R.string.test_report, mTest.getTitle(),
				dateString, finishedString, Integer.toString(result) + "ppm");
		
		return report;
	}

}
