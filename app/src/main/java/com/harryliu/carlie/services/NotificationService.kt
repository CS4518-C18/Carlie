package com.harryliu.carlie.services

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.harryliu.carlie.R

/**
 * @author Yuan Wang
 * @version Feb 16, 2018
 */

object NotificationService {
    private val CHANNEL_ID = "default_channel"
    private var notificationID = 1

    fun showNotification(context: Context, title: String, message: String, activity: Activity): Int {

        val intent = Intent(context, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationID, builder.build())
        return notificationID
    }
}
