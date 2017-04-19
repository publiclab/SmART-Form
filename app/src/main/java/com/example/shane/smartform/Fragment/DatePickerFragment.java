package com.example.shane.smartform.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.example.shane.smartform.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
	
	public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
	
	private Date mDate;
	private int mHour;
	private int mMinute;
	
	public static DatePickerFragment newInstance(Date date){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	private void sendResult(int resultCode){
		if (getTargetFragment() == null){
			return;
		}
		
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
		
		// Create a calendar to extract day month year from Date
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		mHour = calendar.get(Calendar.HOUR);
		mMinute = calendar.get(Calendar.MINUTE);
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
		
		DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_picker);
		datePicker.init(year, month, day, new OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
									  int dayOfMonth) {
				// Translate year, month, day into a Date object using a calendar, maintaining the time
				mDate = new GregorianCalendar(year, monthOfYear, dayOfMonth, mHour, mMinute).getTime();
				
				// Update argument to preserve selected value on rotation
				getArguments().putSerializable(EXTRA_DATE, mDate);
			}
		});
		
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.date_picker_title)
			.setPositiveButton(
					android.R.string.ok, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							sendResult(Activity.RESULT_OK);
						}
					})
			.create();
	}
}
