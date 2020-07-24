package com.bensadiku.dcf.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.bensadiku.dcf.R
import com.bensadiku.dcf.activities.MainActivity
import timber.log.Timber

/**
 * A interactor class used to push / hide notifications
 */
object PushNotification {
    private val hasNotificationsEnabled: Boolean get() = Prefs.getHasNotificationsEnabled()
    private val channelId = Constants.NOTIFICATION_CHANNEL_ID
    private val channelName = Constants.NOTIFICATION_CHANNEL_NAME
    private val notificationId = Constants.NOTIFICATION_ID
    private val notificationExtraKey = Constants.NOTIFICATION_BODY_EXTRA_KEY

    fun show(messageBody: String, context: Context) {
        if (!hasNotificationsEnabled) {
            Timber.w("Notifications are disabled, aborting!")
            return
        }
        val notificationManager: NotificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(notificationExtraKey, messageBody)

        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setContentTitle("The daily cat fact is here.")
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
                    .setBigContentTitle("..and the daily cat fact is")
            )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        notificationManager.notify(notificationId, notification.build())
    }

    //removes the notification that was showing
    fun hide(context: Context) {
        val notificationManager: NotificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.cancel(Constants.NOTIFICATION_ID)
    }
}