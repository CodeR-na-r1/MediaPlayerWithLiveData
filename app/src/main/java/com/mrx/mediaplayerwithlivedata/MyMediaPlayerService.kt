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

class MyMediaPlayerService(val _context: Context) : Service() {

    private val mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    private val liveDataDuration = MutableLiveData(0)
    private val liveDataNowPosition = MutableLiveData(0)

    fun getLiveDataDuration(): LiveData<Int> {
        return liveDataDuration
    }
    fun getLiveDataNowPosition(): LiveData<Int> {
        return liveDataNowPosition
    }

    fun setTrack(trackName: String) {
        pause()
        //TRACK_NAME = trackName
        IS_NEW_TRACK = true
        start()
    }

    fun start() {
        Log.d(TAG, "Service start track -> ${TRACK_NAME}")
        if (!IS_NEW_TRACK) {
            Log.d(TAG, "Service start old track")
            mediaPlayer.start()
        }
        else {
            Log.d(TAG, "Service start new track -> ${TRACK_NAME}")
            val afd = _context.assets.openFd(TRACK_NAME)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

            IS_NEW_TRACK = false

            afd.close()
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

        handler.postDelayed(updateDataTrackSheduler, 1000L)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun pause() {
        Log.d(TAG, "Service pause")
        mediaPlayer.pause()

        handler.removeCallbacks(updateDataTrackSheduler)
    }

    private val updateDataTrackSheduler = Runnable {
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand -> trackname = ${TRACK_NAME}")

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        const val TAG = "myTag"

        val TRACK_NAME: String = "DU_HAST.mp3"
        var IS_NEW_TRACK: Boolean = true

        fun startService(context: Context, trackName: String) {
            //TRACK_NAME = trackName

            context.startService(Intent(context, MyMediaPlayerService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, MyMediaPlayerService::class.java))
        }
    }
}