package com.codewithmehran.wordmaster.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.model.WordMasterApp
import java.util.Calendar
import java.util.Date

private const val CHANNEL_ID = "daily_word_reminder"
private const val NOTIFICATION_ID = 1001

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val app = applicationContext as WordMasterApp
        val streakRepository = app.streakRepository
        val streak = streakRepository.getStreak()

        val todayStart = startOfDay(Date())
        val lastDate = streak.lastAddedDate?.let { startOfDay(it) }

        if (lastDate == null || lastDate.time != todayStart.time) {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            if (currentHour in 19..23) {
                showNotification(currentHour)
            }
        }

        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(hour: Int) {
        createChannel()

        val (title, body) = getNotificationContent(hour)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_word)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(applicationContext).notify(
            NOTIFICATION_ID + hour,
            builder.build()
        )
    }

    private fun getNotificationContent(hour: Int): Pair<String, String> {
        return when (hour) {
            19 -> Pair(
                applicationContext.getString(R.string.notification_title_19),
                applicationContext.getString(R.string.notification_body_19)
            )
            20 -> Pair(
                applicationContext.getString(R.string.notification_title_20),
                applicationContext.getString(R.string.notification_body_20)
            )
            21 -> Pair(
                applicationContext.getString(R.string.notification_title_21),
                applicationContext.getString(R.string.notification_body_21)
            )
            22 -> Pair(
                applicationContext.getString(R.string.notification_title_22),
                applicationContext.getString(R.string.notification_body_22)
            )
            23 -> Pair(
                applicationContext.getString(R.string.notification_title_23),
                applicationContext.getString(R.string.notification_body_23)
            )
            else -> Pair(
                applicationContext.getString(R.string.notification_title),
                applicationContext.getString(R.string.notification_body)
            )
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.notification_channel_name)
            val descriptionText =
                applicationContext.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startOfDay(date: Date): Date {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.time
    }
}