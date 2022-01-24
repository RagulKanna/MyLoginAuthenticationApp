package com.example.myloginapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(context: Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val taskData = inputData.keyValueMap
        val reminderTitle = taskData.getValue("Title")
        val reminderContent = taskData.getValue("Content")
        scheduleNotifications(reminderTitle.toString(), reminderContent.toString())
        return Result.success()
    }

    private fun scheduleNotifications(title: String, description: String) {
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel =
                NotificationChannel("Reminder", "Notes", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext, "Reminder")
            .setContentTitle(title).setContentText(description)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)

        notificationManager.notify(1, builder.build())
    }
}
