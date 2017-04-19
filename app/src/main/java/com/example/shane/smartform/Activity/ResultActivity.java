package com.example.shane.smartform.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.shane.smartform.Activity.CameraActivity;
import com.example.shane.smartform.R;


public class ResultActivity extends Activity{
    private static final String TAG = "ResultActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        Bundle extras = getIntent().getExtras();
        Uri myUri = Uri.parse(extras.getString(CameraActivity.EXTRA_CAMERA_DATA));
        Log.d(TAG, "Get image uri" + myUri.toString());
    }
}
