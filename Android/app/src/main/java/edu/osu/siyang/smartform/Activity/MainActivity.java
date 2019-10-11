package edu.osu.siyang.smartform.Activity;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.osu.siyang.smartform.R;
import edu.osu.siyang.smartform.Fragment.TestListFragment;
import edu.osu.siyang.smartform.Fragment.HealthFragment;
import edu.osu.siyang.smartform.Fragment.ResultFragment;
import edu.osu.siyang.smartform.Fragment.InfoFragment;

/**
 * Created by siyangzhang on 2/10/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FragmentManager fm;

    private LinearLayout mTabIndex, mTabHealth, mTabFind;

    private RelativeLayout mTabResult;

    private ImageView mIndexImg, mHealthImg, mFindImg, mResultImg;

    private Fragment tabindex, tabhealth, tabfind, tabresult;

    private TextView title_text;

    //public static ACache mCache;

    public static final String TAG = "Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mCache= ACache.get(this);

        initView();
        initEvent();
        setSelect(0);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Activity onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Activity onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Activity onPause");
        super.onPause();
    }

    private void initView() {

        mTabIndex = (LinearLayout) findViewById(R.id.id_index);
        mTabHealth = (LinearLayout) findViewById(R.id.id_health);
        mTabFind = (LinearLayout) findViewById(R.id.id_find);
        mTabResult = (RelativeLayout) findViewById(R.id.id_result);

        mIndexImg = (ImageView) findViewById(R.id.id_indeximg);
        mHealthImg = (ImageView) findViewById(R.id.id_healthimg);
        mFindImg = (ImageView) findViewById(R.id.id_findimg);
    }

    private void initEvent() {
        mTabIndex.setOnClickListener(this);
        mTabHealth.setOnClickListener(this);
        mTabFind.setOnClickListener(this);
        mTabResult.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_index:
                setSelect(0);
                break;
            case R.id.id_health:
                setSelect(1);
                break;
            case R.id.id_find:
                setSelect(2);
            case R.id.id_result:
                setSelect(3);
                break;
        }
    }

    private void setSelect(int i) {
        switch (i) {
            case 0:
                if (tabindex == null) {
                    tabindex = new TestListFragment();
                }
                changeFragment(tabindex);
                break;
            case 1:
                if (tabhealth == null) {
                    tabhealth = new HealthFragment();
                }
                changeFragment(tabhealth);
                break;
            case 2:
                if (tabfind == null) {
                    tabfind = new InfoFragment();
                }
                changeFragment(tabfind);
                break;
            case 3:
                if (tabresult == null) {
                    tabresult = new ResultFragment();
                }
                changeFragment(tabresult);
                break;
        }
        setTab(i);

    }

    private void setTab(int i) {
        resetImgs();
        switch (i) {
            case 0:
                mIndexImg.setImageResource(R.drawable.icon_list);
                break;
            case 1:
                mHealthImg.setImageResource(R.drawable.icon_health);
                break;
            case 2:
                mFindImg.setImageResource(R.drawable.icon_info);
                break;
        }
    }
    private void changeFragment(Fragment targetFragment){
        fm = getSupportFragmentManager();
        if(fm!=null) {
            fm.beginTransaction()
                    .replace(R.id.fmcontent, targetFragment, "Fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }
    private void resetImgs() {
        mIndexImg.setImageResource(R.drawable.icon_list);
        mHealthImg.setImageResource(R.drawable.icon_health);
        mFindImg.setImageResource(R.drawable.icon_info);
    }

    Boolean ActionSheetFlag = false;
    private static long firstTime;


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (ActionSheetFlag) {
            super.onBackPressed();
        } else {
            if (firstTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
            }
            firstTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object and put them in your textview
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the values you need from your textview into "outState"-object
        super.onSaveInstanceState(outState);
    }
}
