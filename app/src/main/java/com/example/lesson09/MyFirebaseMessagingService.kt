package com.example.lesson09

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "@@@ MyFirebaseMessagingService"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived() called with: remoteMessage = $remoteMessage")
        Log.d(TAG, "onMessageReceived() called with: remoteMessage.data = ${remoteMessage.data}")
        Log.d(TAG, "onMessageReceived() called with: remoteMessage.data.toMap() = ${remoteMessage.data.toMap()}")
        Log.d(TAG, "onMessageReceived() called with: remoteMessage.notification = ${remoteMessage.notification}")
        Log.d(TAG, "onMessageReceived() called with: remoteMessage.notification?.title = ${remoteMessage.notification?.title}")
        Log.d(TAG, "onMessageReceived() called with: remoteMessage.notification?.body = ${remoteMessage.notification?.body}")
//        Toast.makeText(this,remoteMessage.notification?.title,Toast.LENGTH_SHORT).show()
        remoteMessage.notification?.let {
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }

    @SuppressLint("LongLogTag")
    private fun showNotification(title: String?, message: String?) {
        Log.d(TAG, "showNotification() called with: title = $title, message = $message")
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
                setContentTitle(title)
                setContentText(message)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    private fun createNotificationChannel(notificationManager:
                                          NotificationManager) {
        Log.d(
            TAG,
            "createNotificationChannel() called with: notificationManager = $notificationManager"
        )
        val name = "Channel name"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }
    @SuppressLint("LongLogTag")
    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken() called with: token = $token")
//Отправить токен на сервер
    }
    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = 37
    }
}