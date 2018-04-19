package edu.osu.siyang.smartform.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.osu.siyang.smartform.Util.MyNewIntentService;

/**
 * Created by siyangzhang on 4/18/18.
 */

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, MyNewIntentService.class);
        context.startService(intent1);
    }
}
