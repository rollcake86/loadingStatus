package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.Observer
import com.udacity.util.DownloadUtils
import com.udacity.util.SendData
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import java.io.File


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        createChannel(
            getString(R.string.download_channel_id),
            getString(R.string.downloadimage),
        )
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            when (radioGroup.checkedRadioButtonId) {
                -1 -> makeToast()
                radioButton.id -> download()
                radioButton2.id -> download()
                radioButton3.id -> download()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val query: DownloadManager.Query = DownloadManager.Query()
            query.setFilterById(id!!)
            var cursor = downloadManager.query(query)
            if (!cursor.moveToFirst()) {
                return
            }

            var columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            var status = cursor.getInt(columnIndex)
            var sendData =
                SendData(downloadUtils = getRadioChecked(radioGroup.checkedRadioButtonId), status)
            custom_button.buttonState = ButtonState.Completed
            notificationManager.cancelAll()
            notificationManager.sendNotification(
                getText(R.string.notification_content).toString(),
                sendData, context!!
            )
            custom_button.isEnabled = true
        }
    }

    private fun getRadioChecked(checkedRadioButtonId: Int): DownloadUtils {
        when (checkedRadioButtonId) {
            radioButton.id -> return DownloadUtils.Glide
            radioButton2.id -> return DownloadUtils.LoadApp
            radioButton3.id -> return DownloadUtils.Retrofit
        }
        return DownloadUtils.LoadApp
    }

    private fun makeToast() {
        Toast.makeText(this, getString(R.string.selected_radio), Toast.LENGTH_SHORT).show()
    }

    private fun download() {
        custom_button.buttonState = ButtonState.Loading
        custom_button.isEnabled = false
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {
        val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Time for breakfast"

        val notificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

    }
}
