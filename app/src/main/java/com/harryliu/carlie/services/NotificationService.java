package com.harryliu.carlie.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.PendingIntent;
import android.view.View;
import android.widget.TextView;

import com.harryliu.carlie.R;

/**
 *  @author Yuan Wang
 *  @version Feb 16, 2018
 */

public class NotificationService extends IntentService{

    private NotificationManager mNotificationManager;
    private String mMessage;
    private int mMillis;
    NotificationCompat.Builder builder;


    public NotificationService() {
        super("com.harryliu.carlie.services");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        mMessage = intent.getStringExtra(CommonConstants.EXTRA_MESSAGE);
        mMillis = intent.getIntExtra(CommonConstants.EXTRA_TIMER, CommonConstants.DEFAULT_TIMER_DURATION);

        String action = intent.getAction();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void createNotification(Intent intent, String msg)
    {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Sets up the Snooze and Dismiss action buttons that will appear in the expanded view of the notification.
        Intent dismissIntent = new Intent(this, NotificationService.class);
        dismissIntent.setAction(CommonConstants.ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);
        Intent snoozeIntent = new Intent(this, NotificationService.class);
        snoozeIntent.setAction(CommonConstants.ACTION_SNOOZE);
        PendingIntent piSnooze = PendingIntent.getService(this, 0,
                snoozeIntent, 0);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(getString(R.string.notification))
                .setContentText(getString(R.string.ping))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(2)
                .setLights(Color.BLUE, 5000, 5000)
//                .setSound(
//                        Uri.parse("file:///sdcard/Notifications/hey_listen.mp3"))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .addAction(R.drawable.ic_stat_dismiss,
                        getString(R.string.dismiss), piDismiss)
                .addAction(R.drawable.ic_stat_snooze,
                        getString(R.string.snooze), piSnooze);

        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra(CommonConstants.EXTRA_MESSAGE, msg);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        startTimer(mMillis);
    }
    private void createNotification(NotificationCompat.Builder builder) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(CommonConstants.NOTIFICATION_ID, builder.build());
    }

    private void startTimer(int millis) {
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.timer_start));
        try {
            Thread.sleep(millis);

        } catch (InterruptedException e) {
            Log.d(CommonConstants.DEBUG_TAG, getString(R.string.sleep_error));
        }
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.timer_finished));
        createNotification(builder);
    }
}
//define some common constants.
class CommonConstants
{

    public CommonConstants() {}

    public static final int SNOOZE_DURATION = 20000;
    public static final int DEFAULT_TIMER_DURATION = 2000;
    public static final String ACTION_SNOOZE = "com.harryliu.carlie.services.ACTION_SNOOZE";
    public static final String ACTION_DISMISS = "com.harryliu.carlie.services.ACTION_DISMISS";
    public static final String ACTION_NOTIFY = "com.harryliu.carlie.services.ACTION_NOTIFY";
    public static final String EXTRA_MESSAGE = "com.harryliu.carlie.services.EXTRA_MESSAGE";
    public static final String EXTRA_TIMER = "com.harryliu.carlie.services.EXTRA_TIMER";
    public static final int NOTIFICATION_ID = 001;
    public static final String DEBUG_TAG = "NotificationManager";
}

class ResultActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activitactivity_result.xmly_result);
        String message = getIntent().getStringExtra(CommonConstants.EXTRA_MESSAGE);
        TextView text = (TextView) findViewById(R.id.result_message);
        text.setText(message);
    }

    public void onSnoozeClick(View v) {
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.setAction(CommonConstants.ACTION_SNOOZE);
        startService(intent);
        finish();
    }

    public void onDismissClick(View v) {
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.setAction(CommonConstants.ACTION_DISMISS);
        startService(intent);
        finish();
    }

}

