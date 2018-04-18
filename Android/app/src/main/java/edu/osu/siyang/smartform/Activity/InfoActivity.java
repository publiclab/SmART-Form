package edu.osu.siyang.smartform.Activity;

import android.support.v4.app.Fragment;

import edu.osu.siyang.smartform.Fragment.InfoFragment;

/**
 * Created by siyangzhang on 4/16/18.
 */

public class InfoActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new InfoFragment();
    }

}

