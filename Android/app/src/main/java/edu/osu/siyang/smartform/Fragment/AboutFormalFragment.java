package edu.osu.siyang.smartform.Fragment;

/**
 * Created by siyangzhang on 4/10/18.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.osu.siyang.smartform.Activity.HealthActivity;
import edu.osu.siyang.smartform.Activity.ResultActivity;
import edu.osu.siyang.smartform.Activity.InfoActivity;
import edu.osu.siyang.smartform.Activity.TestListActivity;
import edu.osu.siyang.smartform.R;

public class AboutFormalFragment extends Fragment {
    private LinearLayout mTabIndex, mTabHealth, mTabFind;
    private ImageView mImgIndex, mImgHealth, mImgFind;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static String uniqueID = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_aboutformaldehyde, container, false);


        /******************************************************/
        // Test list
        mTabIndex = (LinearLayout) v.findViewById(R.id.id_index);
        mImgIndex = (ImageView) v.findViewById(R.id.id_indeximg);
        mImgIndex.setImageResource(R.drawable.icon_list);

        mTabIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TestListActivity.class);
                startActivity(i);
            }
        });

        // Health survey
        mTabHealth = (LinearLayout) v.findViewById(R.id.id_health);
        mImgHealth = (ImageView) v.findViewById(R.id.id_healthimg);
        mImgHealth.setImageResource(R.drawable.health_pressed);


        // User survey
        mTabFind = (LinearLayout) v.findViewById(R.id.id_find);
        mImgFind = (ImageView) v.findViewById(R.id.id_findimg);
        mImgFind.setImageResource(R.drawable.icon_info);

        mTabFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), InfoActivity.class);
                startActivity(i);
            }
        });

        return v;
    }
}