package com.mrx.mediaplayerwithlivedata

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MediaPlayerReceiver : BroadcastReceiver() {
    private val liveDataDuration by lazy { MutableLiveData<String>("") }
    fun getLiveDataDuration(): LiveData<String> {
        return liveDataDuration
    }

    private val liveDataNowPosition by lazy { MutableLiveData<String>("") }
    fun getLiveDataNowPosition(): LiveData<String> {
        return liveDataNowPosition
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(MediaPlayerService.TAG, "MediaPlayerReceiver onReceive")

        when (intent.getStringExtra(BROADCAST_CONSTANTS.STRING_DATA_TYPE_KEY.value)) {
            BROADCAST_CONSTANTS.STRING_DATA_DURATION_KEY.value -> {
                Log.d("myTag", "MediaPlayerReceiver DURATION_TRACK")

                liveDataDuration.value = intent.getStringExtra(BROADCAST_CONSTANTS.DURATION_TRACK.value)
            }
            BROADCAST_CONSTANTS.NOW_POSITION_TRACK_KEY.value -> {
                Log.d("myTag", "MediaPlayerReceiver NOW_POSITION_TRACK")

                liveDataNowPosition.value = intent.getStringExtra(BROADCAST_CONSTANTS.NOW_POSITION_TRACK.value)
            }
        }
    }
}