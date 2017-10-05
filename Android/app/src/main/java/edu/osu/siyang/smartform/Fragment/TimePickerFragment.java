package edu.osu.siyang.smartform.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {
	public static final String EXTRA_TIME = "com.bignerdranche.android.criminalintent.time";
	public static final String TAG_KIO = "KIO";
	
	private Date mDate;
	
	public static TimePickerFragment newInstance(Date date){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TIME, date);
		
		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	private void sendResult(int resultCode){
		Log.d(TAG_KIO, "Inside sendResult");
		if ( getTargetFragment() == null) {
			return;
		}
		
		Log.d(TAG_KIO, "mDate is: " + mDate.getTime());
		
		Intent i = new Intent();
		i.putExtra(EXTRA_TIME, mDate);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		// Get date from the Extra
		mDate = (Date) getArguments().getSerializable(EXTRA_TIME);
		
		// Create a calendar so you can access hour and minutes
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);

		// Inflate the Time Dialog View
		View v = getActivity().getLayoutInflater().inflate(edu.osu.siyang.smartform.R.layout.dialog_time, null);
		
		// Get the time picker and set hour and minute
		TimePicker timePicker = (TimePicker) v.findViewById(edu.osu.siyang.smartform.R.id.dialog_time_picker);
		timePicker.setCurrentHour( calendar.get(Calendar.HOUR) );
		timePicker.setCurrentMinute( calendar.get(Calendar.MINUTE) );
		
		timePicker.setOnTimeChangedListener( new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				Log.d(TAG_KIO, "mDate: " + mDate.toString());
				Log.d(TAG_KIO, "mDate time: "+mDate.getTime());
				Log.d(TAG_KIO, "hourOfDay: " + hourOfDay);
				Log.d(TAG_KIO, "minute: " + minute);
				
				// KIO - This is how you use calendar!
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(mDate);
				calendar.set(Calendar.HOUR, hourOfDay);
				calendar.set(Calendar.MINUTE, minute);
				mDate.setTime( calendar.getTimeInMillis() );
				
				Log.d(TAG_KIO, "mDate after: "+mDate.toString());
				Log.d(TAG_KIO, "mDate time after: "+mDate.getTime());
				
				getArguments().putSerializable(EXTRA_TIME, mDate);
			}
		});
	
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(edu.osu.siyang.smartform.R.string.time_picker_title)
			.setPositiveButton(
					android.R.string.ok,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d(TAG_KIO, "Time Picker OK has been clicked");
							sendResult(Activity.RESULT_OK);
						}
					})
			.create();
	}	
}
