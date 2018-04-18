package edu.osu.siyang.smartform.Activity;

import android.support.v4.app.Fragment;

import edu.osu.siyang.smartform.Fragment.HealthFragment;

/**
 * Created by siyangzhang on 4/16/18.
 */

public class HealthActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new HealthFragment();
    }

}

