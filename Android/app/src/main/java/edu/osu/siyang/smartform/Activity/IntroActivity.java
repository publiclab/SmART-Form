package edu.osu.siyang.smartform.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import android.graphics.Color;
import android.widget.Toast;

import com.hololo.tutorial.library.PermissionStep;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import java.util.ArrayList;
import java.util.List;

import edu.osu.siyang.smartform.R;

public class IntroActivity extends TutorialActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(IntroActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("Network");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("CAMERA");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read external storage");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write external storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addFragment(new Step.Builder().setTitle("Start new tests here")
                .setContent("Access multiple tests below")
                .setBackgroundColor(Color.parseColor("#000000")) // int background color
                .setDrawable(R.drawable.app_intro1) // int top drawable
                .build());

        addFragment(new Step.Builder().setTitle("Peel off sticker")
                .setContent("Make sure the badge is exposed")
                .setBackgroundColor(Color.parseColor("#000000")) // int background color
                .setDrawable(R.drawable.app_intro2) // int top drawable
                .build());

        addFragment(new Step.Builder().setTitle("Take 'before' picture")
                .setContent("Then wait 72 hours")
                .setBackgroundColor(Color.parseColor("#000000")) // int background color
                .setDrawable(R.drawable.app_intro3) // int top drawable
                .build());

        addFragment(new Step.Builder().setTitle("After 72 hours, take 'after' picture")
                .setContent("Ratake images if you see warnings")
                .setBackgroundColor(Color.parseColor("#000000")) // int background color
                .setDrawable(R.drawable.app_intro4) // int top drawable
                .build());

        addFragment(new Step.Builder().setTitle("Contribute your data!")
                .setContent("Take the surveys")
                .setBackgroundColor(Color.parseColor("#000000")) // int background color
                .setDrawable(R.drawable.app_intro5) // int top drawable
                .build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
    }

    @Override
    public void finishTutorial() {
        // Your implementation
        Intent i = new Intent(this, TestListActivity.class);
        startActivity(i);    }
}