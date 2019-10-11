package edu.osu.siyang.smartform.Activity;

/**
 * Created by siyangzhang on 4/10/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.osu.siyang.smartform.Fragment.HealthFragment;
import edu.osu.siyang.smartform.Fragment.TestListFragment;
import edu.osu.siyang.smartform.Fragment.ResultFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TestListFragment tab1 = new TestListFragment();
                return tab1;
            case 1:
                HealthFragment tab2 = new HealthFragment();
                return tab2;
            case 2:
                ResultFragment tab3 = new ResultFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}