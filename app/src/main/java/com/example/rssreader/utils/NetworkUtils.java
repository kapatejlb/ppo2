package com.example.rssreader.utils;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rssreader.MainActivity;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

public class NetworkUtils extends GcmTaskService {

    public static final String EXTRA_TAG = "extra_tag";
    public static final String ACTION_DONE = "GcmTaskService#ACTION_DONE";


    @Override
    public int onRunTask(TaskParams taskParams) {

        String tag = taskParams.getTag();
        int result = GcmNetworkManager.RESULT_SUCCESS;


        // Create Intent to broadcast the task information.
        Intent intent = new Intent();
        intent.setAction(ACTION_DONE);
        intent.putExtra(EXTRA_TAG, tag);

        // Send local broadcast, running Activities will be notified about the task.
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(intent);

        // Return one of RESULT_SUCCESS, RESULT_FAILURE, or RESULT_RESCHEDULE
        return result;
    }
}
