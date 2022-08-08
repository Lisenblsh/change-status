package com.example.changesatus.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.changesatus.R
import okhttp3.*
import java.io.IOException

class Broadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {
            if (intent.hasExtra("all")) {
                setStatus(context, 0)
                showNotification(context, "Невидимка отключена")
            } else if (intent.hasExtra("only_me")) {
                setStatus(context, 3)
                showNotification(context, "Невидимка включена")
            }
        }
    }


    private fun setStatus(context: Context, id: Int) {
        val pref = context.getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        val list = context.resources.getStringArray(R.array.privacy_type_for_request)
        val privacyType = list[id]
        val url =
            "https://api.vk.me/method/account.setPrivacy?v=5.109&key=online&value=$privacyType&access_token="
        val userAgent = "VKAndroidApp/1.777-777 (Android 777; SDK 777; bagosi; 1; ru; 777x777)"
        val client = OkHttpClient().newBuilder()
            .build()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .addHeader("User-Agent", userAgent).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

            }

            override fun onFailure(call: Call, e: IOException) {

            }
        })

        if (pref != null) {
            with(pref.edit()) {
                putInt("PRIVACY_ID", id)
                apply()
            }
        }
    }

    private val channelId = "CHANNEL_ID"

    private fun showNotification(context: Context, notifyBody: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intentSetAll = Intent(context, Broadcast::class.java)
            .putExtra("all", true)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntentSetAll = PendingIntent
            .getBroadcast(
                context,
                0,
                intentSetAll,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val intentSetOnlyMe = Intent(context, Broadcast::class.java)
            .putExtra("only_me", true)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntentSetOnlyMe = PendingIntent
            .getBroadcast(
                context,
                1,
                intentSetOnlyMe,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )


        val builder = NotificationCompat.Builder(context.applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setOngoing(true)
            .setSilent(true)
            .setShowWhen(false)
            .setContentTitle(notifyBody)
            .addAction(R.drawable.ic_launcher_foreground, "Выкл. невидимку", pendingIntentSetAll)
            .addAction(R.drawable.ic_launcher_foreground, "Вкл. невидимку", pendingIntentSetOnlyMe)

        val channel =
            NotificationChannel(channelId, "Custom notify", NotificationManager.IMPORTANCE_LOW)

        channel.enableVibration(false)
        notificationManager.createNotificationChannel(channel)
        builder.setChannelId(channelId)

        val notification = builder.build()

        notificationManager.notify(1000, notification)

    }
}