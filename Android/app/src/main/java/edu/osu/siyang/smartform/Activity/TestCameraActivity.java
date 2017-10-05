package edu.osu.siyang.smartform.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import edu.osu.siyang.smartform.Fragment.TestCameraFragment;

public class TestCameraActivity extends SingleFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Hide the window title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Hide status bar and other OS-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected Fragment createFragment() {
		return new TestCameraFragment();
	}
	
}
