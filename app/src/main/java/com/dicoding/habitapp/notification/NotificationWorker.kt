package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
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
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Habit Reminder")
            .setContentText("Don't forget to do your habit: $habitTitle")
            .setSmallIcon(R.drawable.ic_notifications).setContentIntent(createPendingIntent())
            .setAutoCancel(true).build()

        createNotificationChannel(notificationManager)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, DetailHabitActivity::class.java)
        intent.putExtra(HABIT_ID, habitId)

        return PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Habit Notifications", NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "habit_channel"
        const val NOTIFICATION_ID = 1
    }

}
