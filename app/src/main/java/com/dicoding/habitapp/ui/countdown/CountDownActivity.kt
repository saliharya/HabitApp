package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_TITLE
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = getParcelableExtra(intent, HABIT, Habit::class.java)

        if (habit != null) {
            findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

            val viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]

            //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
            viewModel.setInitialTime(habit.minutesFocus)

            viewModel.currentTimeString.observe(this) {
                findViewById<TextView>(R.id.tv_count_down).text = it
            }

            viewModel.eventCountDownFinish.observe(this) { eventCountDownFinish ->
                if (eventCountDownFinish) {
                    updateButtonState(false)
                }

                val notificationWorkRequest =
                    OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                        .setInputData(createInputData()).build()

                WorkManager.getInstance(this).enqueue(notificationWorkRequest)
            }

            //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
            val workManager = WorkManager.getInstance(this)
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>().setInitialDelay(
                habit.minutesFocus, TimeUnit.MINUTES
            ).build()

            findViewById<Button>(R.id.btn_start).setOnClickListener {
                viewModel.startTimer()
                workManager.enqueue(workRequest)
                updateButtonState(true)
            }

            findViewById<Button>(R.id.btn_stop).setOnClickListener {
                viewModel.resetTimer()
                workManager.cancelWorkById(workRequest.id)
                updateButtonState(false)
            }
        }

    }

    private fun createInputData(): Data {
        return Data.Builder().putInt(NotificationWorker.HABIT_ID, 1)
            .putString(NotificationWorker.HABIT_TITLE, HABIT_TITLE).build()
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}