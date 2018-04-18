package edu.osu.siyang.smartform.Fragment;

/**
 * Created by siyangzhang on 4/10/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import edu.osu.siyang.smartform.Activity.HealthActivity;
import edu.osu.siyang.smartform.Activity.IntroActivity;
import edu.osu.siyang.smartform.Activity.TestListActivity;
import edu.osu.siyang.smartform.R;

public class InfoFragment extends Fragment {
    private LinearLayout mTabIndex, mTabHealth, mTabFind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_info, container, false);

        // Test list
        mTabIndex = (LinearLayout) v.findViewById(R.id.id_index);
        mTabIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TestListActivity.class);
                startActivityForResult(i, 0);
            }
        });

        // Health survey
        mTabHealth = (LinearLayout) v.findViewById(R.id.id_class);
        mTabHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), HealthActivity.class);
                startActivityForResult(i, 0);
            }
        });

        // User survey
        mTabFind = (LinearLayout) v.findViewById(R.id.id_find);


        return v;
    }
}