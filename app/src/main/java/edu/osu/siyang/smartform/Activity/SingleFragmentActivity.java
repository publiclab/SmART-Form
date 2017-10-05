package edu.osu.siyang.smartform.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
	
	protected abstract Fragment createFragment();
	
	protected int getLayoutResId(){
		return edu.osu.siyang.smartform.R.layout.activity_fragment;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResId());
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(edu.osu.siyang.smartform.R.id.fragmentContainer);
		
		if( fragment == null ){
			fragment = createFragment();
			fm.beginTransaction()
				.add(edu.osu.siyang.smartform.R.id.fragmentContainer, fragment)
				.commit();
		}
	}

}
