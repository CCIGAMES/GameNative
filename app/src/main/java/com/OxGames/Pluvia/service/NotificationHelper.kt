package com.OxGames.Pluvia.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.OxGames.Pluvia.MainActivity
import com.OxGames.Pluvia.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "pluvia_foreground_service"
        private const val NOTIFICATION_ID = 1

        const val ACTION_EXIT = "com.oxgames.pluvia.EXIT"
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.desc_fg_notification_title),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.desc_fg_notification)
            setShowBadge(false)
        }

        notificationManager.createNotificationChannel(channel)
    }

    fun notify(content: String) {
        val notification = createForegroundNotification(content)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancel() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun createForegroundNotification(content: String): Notification {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "pluvia://home".toUri(),
            context,
            MainActivity::class.java,
        ).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val stopIntent = Intent(context, SteamService::class.java).apply {
            action = ACTION_EXIT
        }
        val stopPendingIntent = PendingIntent.getForegroundService(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(0, context.getString(R.string.exit), stopPendingIntent) // 0 = no icon
            .build()
    }
}
