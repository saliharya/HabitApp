package com.dicoding.habitapp.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitTitle = inputData.getString(HABIT_TITLE)

    override fun doWork(): Result {
        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify =
            prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent
        if (shouldNotify) {
            showNotification()
        }

        return Result.success()
    }

    private fun showNotification() {
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, DetailHabitActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications).setContentTitle(habitTitle)
                .setContentText(applicationContext.getString(R.string.notify_content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent)
                .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}