package com.dicoding.todoapp.notification

import com.dicoding.todoapp.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.setting.SettingsActivity
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.ui.list.TaskActivity
import com.dicoding.todoapp.utils.*


class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)
    private val dateNotification = inputData.getString(NOTIFICATION_CONTENT)


    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun doWork(): Result {
        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent


        val task = TaskRepository.getInstance(this.applicationContext).getNearestActiveTask()

        val date = DateConverter.convertMillisToString(task.dueDateMillis)
        val content = applicationContext.getString(R.string.notify_content, date)


        val notificationIntent = Intent(applicationContext, TaskActivity::class.java)
        val taskStackBuilder: android.app.TaskStackBuilder = android.app.TaskStackBuilder.create(applicationContext)
        taskStackBuilder.addParentStack(SettingsActivity::class.java)
        taskStackBuilder.addNextIntent(notificationIntent)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentIntent(getPendingIntent(task))
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(task.title)
            .setContentText(content)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(ringtone)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            if (channelName != null) {
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            }

            notificationManager.createNotificationChannel(channel)

        }

        mBuilder.setAutoCancel(true)
        val notification = mBuilder?.build()
        notification?.flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONGOING_EVENT
        notificationManager.notify(NOTIFICATION_CHANNEL_ID_INT, notification)

        return Result.success()
    }


}
