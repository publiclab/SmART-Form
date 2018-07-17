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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.osu.siyang.smartform.Activity.AboutFormalActivity;
import edu.osu.siyang.smartform.Activity.HealthActivity;
import edu.osu.siyang.smartform.Activity.InfoActivity;
import edu.osu.siyang.smartform.Activity.TestListActivity;
import edu.osu.siyang.smartform.R;

public class DataFragment extends Fragment {
    private LinearLayout mTabIndex, mTabHealth, mTabFind;
    private TextView mText;
    private Button mBtn, dataBtn;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static String uniqueID = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_data, container, false);

        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE);
        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

        mText = (TextView) v.findViewById(R.id.text_id1_d);
        mText.setText(uniqueID);

        mBtn = (Button) v.findViewById(R.id.copy_id1_d);
        View.OnClickListener copy = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, uniqueID);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "User ID is copyed to clipboard!", Toast.LENGTH_SHORT).show();
            }
        };
        mBtn.setOnClickListener(copy);

        dataBtn = (Button) v.findViewById(R.id.data_survey_btn);
        dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://osu.az1.qualtrics.com/jfe/form/SV_bNrGYLQ1uKGWmKF"));
                startActivity(browserIntent);
            }
        });

        /******************************************************/
        // Test list
        mTabIndex = (LinearLayout) v.findViewById(R.id.id_index);
        mTabIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TestListActivity.class);
                startActivity(i);
            }
        });

        // Health survey
        mTabHealth = (LinearLayout) v.findViewById(R.id.id_health);
        mTabHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AboutFormalActivity.class);
                startActivity(i);
            }
        });

        // User survey
        mTabFind = (LinearLayout) v.findViewById(R.id.id_find);
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