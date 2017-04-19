package com.example.shane.smartform.Activity;

import com.example.shane.smartform.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

/**
 * Created by siyangzhang on 4/6/17.
 */

/**
 * This file provides simple End User License Agreement
 * It shows a simple dialog with the license text, and two buttons.
 * If user clicks on 'cancel' button, app closes and user will not be granted access to app.
 * If user clicks on 'accept' button, app access is allowed and this choice is saved in preferences
 * so next time this will not show, until next upgrade.
 */

public class AppEULA {
    private String EULA_PREFIX = "appeula";
    private Activity mContext;

    public AppEULA(Activity context) {
        mContext = context;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    public void show() {
        PackageInfo versionInfo = getPackageInfo();

        // The eulaKey changes every time you increment the version number in
        // the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        boolean bAlreadyAccepted = prefs.getBoolean(eulaKey, false);
        if (bAlreadyAccepted == false) {

            // EULA title
            String title = mContext.getString(R.string.app_name) + "v"
            + versionInfo.versionName;

            // EULA text
            String message = mContext.getString(R.string.eula);

            // Disable orientation changes, to prevent parent activity
            // reinitialization
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.agree,
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialogInterface, int i) {
                                    // Mark this version as read.
                                    SharedPreferences.Editor editor = prefs
                                            .edit();
                                    editor.putBoolean(eulaKey, true);
                                    editor.commit();

                                    // Close dialog
                                    dialogInterface.dismiss();

                                    // Enable orientation changes based on
                                    // device's sensor
                                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                }
                            })
                    .setNegativeButton(R.string.disagree,
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Close the activity as they have declined
                                    // the EULA
                                    mContext.finish();
                                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                }

                            });
            builder.create().show();
        }
    }
}
