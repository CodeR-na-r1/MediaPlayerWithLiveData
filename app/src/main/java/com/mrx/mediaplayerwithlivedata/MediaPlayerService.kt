package com.mrx.mediaplayerwithlivedata

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MediaPlayerService : Service() {

    inner class ServiceMediaPlayerReciever() : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Service onReceive")

            if (intent.getStringExtra(BROADCAST_CONSTANTS.STRING_DATA_TYPE_KEY.value) == BROADCAST_CONSTANTS.SWITCH_STATE.value) {
                Log.d(TAG, "Service onReceive BROACAST_SWITCH_STATE")

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    handler.removeCallbacks(sendSheduler)
                }
                else {
                    mediaPlayer.start()
                    handler.postDelayed(sendSheduler, 1000L)
                }
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val mediaPlayer = MediaPlayer()

    private val sendSheduler: Runnable = Runnable {
        if (mediaPlayer.isPlaying) {
            sendler()
        }
    }

    private fun sendler() {
        GlobalScope.launch(Dispatchers.Main) {
            sendBroadcast(Intent().apply {
                action = BROADCAST_CONSTANTS.ID.value
                putExtra(BROADCAST_CONSTANTS.STRING_DATA_TYPE_KEY.value, BROADCAST_CONSTANTS.STRING_DATA_DURATION_KEY.value)
                putExtra(BROADCAST_CONSTANTS.DURATION_TRACK.value, mediaPlayer.duration.toString())
            })

            sendBroadcast(Intent().apply {
                action = BROADCAST_CONSTANTS.ID.value
                putExtra(BROADCAST_CONSTANTS.STRING_DATA_TYPE_KEY.value, BROADCAST_CONSTANTS.NOW_POSITION_TRACK_KEY.value)
                putExtra(BROADCAST_CONSTANTS.NOW_POSITION_TRACK.value, mediaPlayer.currentPosition.toString())
            })

            handler.postDelayed(sendSheduler, 1000L)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        super.onDestroy()

        handler.removeCallbacksAndMessages(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand")

        val afd = assets.openFd(TRACK_NAME!!)
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

        afd.close()
        mediaPlayer.prepare()

        registerReceiver(ServiceMediaPlayerReciever(), IntentFilter(BROADCAST_CONSTANTS.ID.value))

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        const val TAG = "myTag"

        var TRACK_NAME: String? = null

        fun startService(context: Context, trackName: String) {
            TRACK_NAME = trackName

            context.startService(Intent(context, MediaPlayerService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, MediaPlayerService::class.java))
        }
    }
}