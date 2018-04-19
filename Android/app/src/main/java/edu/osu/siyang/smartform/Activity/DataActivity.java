package edu.osu.siyang.smartform.Activity;

import android.support.v4.app.Fragment;

import edu.osu.siyang.smartform.Fragment.DataFragment;

/**
 * Created by siyangzhang on 4/16/18.
 */

public class DataActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DataFragment();
    }

}

