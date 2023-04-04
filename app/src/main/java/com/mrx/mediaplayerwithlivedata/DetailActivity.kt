package com.mrx.mediaplayerwithlivedata

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.mrx.mediaplayerwithlivedata.databinding.ActivityDetailBinding
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    var isPlaying = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isPlaying = intent.getBooleanExtra(BROADCAST_CONSTANTS.STRING_DATA_IS_PLAYING_KEY.value, false)
        updatePlayOrStopButton()

        binding.progressBar.min = 0

        val mediaPlayerReceiver = MediaPlayerReceiver()
        registerReceiver(mediaPlayerReceiver, IntentFilter(BROADCAST_CONSTANTS.ID.value))

        mediaPlayerReceiver.getLiveDataDuration().observe(this) {
            if (it != "") {
                binding.durationTrack.text = "${TimeUnit.MILLISECONDS.toMinutes(it.toLong())}" +
                        ":" +
                        "${TimeUnit.MILLISECONDS.toSeconds(it.toLong()) % 60}"
                binding.progressBar.max = it.toInt()
            }
        }

        mediaPlayerReceiver.getLiveDataNowPosition().observe(this) {
            if (it != "") {
                binding.nowPositionTrack.text = "${TimeUnit.MILLISECONDS.toMinutes(it.toLong())}" +
                        ":" +
                        "${TimeUnit.MILLISECONDS.toSeconds(it.toLong()) % 60}"
                binding.progressBar.progress = it.toInt()
            }
        }

        binding.playOrStop.setOnClickListener(playOrStopButtonListener)
    }

    override fun onPause() {
        super.onPause()

        MainActivity.IS_PLAYING = isPlaying
    }

    private val playOrStopButtonListener = { view: View ->
        sendBroadcast(Intent().apply {
            action = BROADCAST_CONSTANTS.ID.value
            putExtra(BROADCAST_CONSTANTS.STRING_DATA_TYPE_KEY.value, BROADCAST_CONSTANTS.SWITCH_STATE.value)
        })

        isPlaying = !isPlaying
        updatePlayOrStopButton()
    }

    private fun updatePlayOrStopButton() {
        if (isPlaying)
            binding.playOrStop.setImageResource(R.drawable.pause)
        else
            binding.playOrStop.setImageResource(R.drawable.play)
    }

    companion object {
        const val TAG = "myTag"
    }
}