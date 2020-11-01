package com.bkav.musicapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {

    public static final String BROADCAST_ACTION = "Broadcast_action";
    public static final String NOTIFICATION_ACTION_NAME = "Notification_action_name";

    /**
     * Send broadcast with action get from notification
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent customIntent = new Intent(BROADCAST_ACTION);
        customIntent.putExtra(NOTIFICATION_ACTION_NAME, intent.getAction());
        context.sendBroadcast(customIntent);
    }
}
