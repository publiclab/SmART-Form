package com.example.shane.smartform.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.shane.smartform.R;
import com.example.shane.smartform.Bean.Test;
import com.example.shane.smartform.Fragment.TestFragment;
import com.example.shane.smartform.Fragment.TestListFragment;

public class TestListActivity extends SingleFragmentActivity
		implements TestListFragment.Callbacks, TestFragment.Callbacks {

	@Override
	protected Fragment createFragment() {
		return new TestListFragment();
	}
	
	@Override
	protected int getLayoutResId(){
		return R.layout.activity_masterdetail;
	}
	
	public void onTestSelected(Test test){
		if( findViewById(R.id.detailFragmentContainer) == null){
			// Start an instance of TestPagerActivity
			Intent i = new Intent(this, TestPagerActivity.class);
			i.putExtra(TestFragment.EXTRA_TEST_ID, test.getId());
			startActivity(i);
		}
		else {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = TestFragment.newInstance(test.getId());
			
			if ( oldDetail != null ){
				ft.remove(oldDetail);
			}
			
			ft.add(R.id.detailFragmentContainer, newDetail);
			ft.commit();
		}
	}
	
	public void onTestUpdated(Test test){
		FragmentManager fm = getSupportFragmentManager();
		TestListFragment listFragment = (TestListFragment) fm.findFragmentById(R.id.fragmentContainer);
		listFragment.updateUI();
	}

}
