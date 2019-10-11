package edu.osu.siyang.smartform.Activity;

import android.support.v4.app.Fragment;

import edu.osu.siyang.smartform.Fragment.ResultFragment;

/**
 * Created by bihechen on 11/25/18.
 */

public class ResultActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ResultFragment();
    }

}

