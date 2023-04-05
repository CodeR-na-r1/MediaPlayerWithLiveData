package com.mrx.mediaplayerwithlivedata

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

const val TAG = "myTag"

class MyMediaPlayerService(): Service() {

    /* fields */

    private val mediaPlayer = MediaPlayer()

    var IS_NEW_TRACK = true

    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand -> trackname = ${TRACK_NAME.value}")

        TRACK_NAME.observeForever() {
            setTrack()
        }

        isPlaying.observeForever() {
            when(it) {
                true ->
                    start()
                false ->
                    pause()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    /* methods */

    fun setTrack() {
        IS_NEW_TRACK = true

        if (isPlaying.value == true) {
            pause()

            start()
        }
    }

    fun start() {
        Log.d(TAG, "Service startTrack -> ${TRACK_NAME.value}")

        if (!IS_NEW_TRACK) {
            Log.d(TAG, "Service start old track")
            mediaPlayer.start()
        } else {
            Log.d(TAG, "Service start new track -> ${TRACK_NAME.value}")
            val afd = assets.openFd(TRACK_NAME.value!!)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

            IS_NEW_TRACK = false

            afd.close()
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

        handler.postDelayed(updateDataTrackSheduler, 1000L)
    }

    fun pause() {
        Log.d(TAG, "Service Trackpause")
        mediaPlayer.pause()

        handler.removeCallbacks(updateDataTrackSheduler)
    }

    private val updateDataTrackSheduler = Runnable {
        Log.d(TAG, "Service updateDataTrackSheduler")

        if (mediaPlayer.isPlaying) {
            updateDataTrack()
        }
    }

    private fun updateDataTrack() {
        Log.d(TAG, "Service updateDataTrack")

        liveDataDuration.value = mediaPlayer.duration
        liveDataNowPosition.value = mediaPlayer.currentPosition

        handler.postDelayed(updateDataTrackSheduler, 1000)
    }

    companion object {
        /* fields */

        val TRACK_NAME = MutableLiveData<String>()
        val isPlaying = MutableLiveData<Boolean>()

        private val liveDataDuration = MutableLiveData(0)
        private val liveDataNowPosition = MutableLiveData(0)

        /* getters */

        fun getLiveDataDuration(): LiveData<Int> {
            return liveDataDuration
        }
        fun getLiveDataNowPosition(): LiveData<Int> {
            return liveDataNowPosition
        }

        fun startService(context: Context) {
            context.startService(Intent(context, MyMediaPlayerService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, MyMediaPlayerService::class.java))
        }
    }
}