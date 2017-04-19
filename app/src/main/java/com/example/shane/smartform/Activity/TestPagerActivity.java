package com.example.shane.smartform.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.shane.smartform.R;
import com.example.shane.smartform.Bean.Test;
import com.example.shane.smartform.Fragment.TestFragment;
import com.example.shane.smartform.Bean.TestLab;

import java.util.ArrayList;
import java.util.UUID;

public class TestPagerActivity extends FragmentActivity
		implements TestFragment.Callbacks {
	
	private ViewPager mViewPager;
	private ArrayList<Test> mTests;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create view programmatically
		mViewPager = new ViewPager(this );
		mViewPager .setId(R.id.viewPager);
		
		// Set the view to this activity
		setContentView(mViewPager);
		
		// Get data
		mTests = TestLab.get(this).getTests();
		
		// Implement FragmentStatePagerAdapter
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm){
			
			@Override
			public int getCount() {
				return mTests.size();
			}
			
			@Override
			public Fragment getItem(int pos){
				Test test = mTests.get(pos);
				return TestFragment.newInstance(test.getId());
			}
			
		});
		
		// Implement the Page Change Listener
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				Test test = mTests.get(pos);
				if(test.getTitle() != null){
					setTitle(test.getTitle());
				}
			}
			
			// Do Nothing for these.
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// Tells you where your page is GOING to be
			}
			@Override
			public void onPageScrollStateChanged(int arg0) { 
				// Tells you if your page is actively being dragged
			}
		});
		
		// Loop through each Test's UUID to find which one should be displayed
		UUID testId = (UUID)getIntent().getSerializableExtra(TestFragment.EXTRA_TEST_ID);
		for(int i = 0; i < mTests.size(); i++){
			if(mTests.get(i).getId().equals(testId)){
				mViewPager.setCurrentItem(i);
				break;
			}
		}
		
	}
	
	// Empty method b/c any activity htat hosts TestFragment must implement TestFragment.Callbacks
	public void onTestUpdated(Test test){
		
	}
}
