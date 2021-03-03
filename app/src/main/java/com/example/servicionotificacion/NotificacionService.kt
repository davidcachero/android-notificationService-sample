package com.example.servicionotificacion

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class NotificacionService : Service() {
    lateinit var job: Job
    val channelId = "CanalNotificationExample"
    var notificationManager: NotificationManager? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"
    }

    override fun onDestroy() {
        runBlocking {
            job.cancel()
        }
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread.sleep(10000)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var canal =
            NotificationChannel(channelId, "Nombre canal", NotificationManager.IMPORTANCE_DEFAULT)
        canal.description = "Descripcion"
        canal.enableVibration(true)
        canal.vibrationPattern = longArrayOf(100, 100, 500, 200)
        notificationManager!!.createNotificationChannel(canal)
        job = MainScope().launch {
            val notificationIntent = Intent(applicationContext, MainActivity::class.java)
            notificationIntent.putExtra("NotificationMessage", "true")
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            notificationIntent.action = Intent.ACTION_MAIN
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val resultIntent =
                PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
            val mBuilder =
                NotificationCompat.Builder(applicationContext, default_notification_channel_id)
                    .setSmallIcon(R.drawable.ic_dialog_alert)
                    .setContentTitle("Notificacion")
                    .setContentText("Contenido de la notificacion")
                    .setAutoCancel(true)
                    .setContentIntent(resultIntent)
            val mNotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                assert(mNotificationManager != null)
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
            assert(mNotificationManager != null)
            mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
            stopService(intent)
        }
        return START_NOT_STICKY
    }
}