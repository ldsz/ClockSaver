package com.ldsz.clocksaver;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class NLService extends NotificationListenerService {
    private static final String TAG = "NSClockSaver";
    static NLService _this;
    private static final boolean VERBOSE = true;

    public static NLService get() {
        return _this;
    }

    @Override
    public void onListenerConnected() {
        if(VERBOSE) Log.i(TAG, "Connected");
        _this = this;
    }

    @Override
    public void onListenerDisconnected() {
        if(VERBOSE) Log.i(TAG, "Disconnected");
        _this = null;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(sbn.getTag() != null) {
            if (sbn.getTag().equals("Notification.NowPlaying")) {
                String Text = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                String title = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
                Log.i(TAG, "Cover : " + sbn.getNotification().extras.get(Notification.EXTRA_LARGE_ICON_BIG));
                Bitmap i = (Bitmap) sbn.getNotification().extras.get(Notification.EXTRA_PICTURE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                i.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] A = stream.toByteArray();
                String[] artist = Text.split("‧");
                Log.i(TAG, "NowPlaying : " + title + " / " + artist[1]);

                //Send to ServiceSaver
                Intent sender = new Intent(ServiceSaver.CS_MSG_1);
                sender.putExtra("title", title);
                sender.putExtra("artist", artist[1].substring(1));
                sender.putExtra("cover", A);
                sendBroadcast(sender);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Notification removed");
        if(sbn.getTag() != null) {
            if (sbn.getTag().equals("Notification.NowPlaying")) {
                Log.i(TAG, "Notification NowPlaying removed");

                //Send to ServiceSaver
                Intent sender = new Intent(ServiceSaver.CS_MSG_1);
                sender.putExtra("title", "Notification_is_Removed_");
                sender.putExtra("artist", "Removed");
                sender.putExtra("cover", "Removed");
                sendBroadcast(sender);
            }
        }
        super.onNotificationRemoved(sbn);
    }
}