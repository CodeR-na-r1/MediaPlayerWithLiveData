package com.mrx.mediaplayerwithlivedata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mrx.mediaplayerwithlivedata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTag", "onDestroy")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MediaPlayerService.startService(applicationContext, TRACK_NAME)

        binding.playOrStop.setOnClickListener(playOrStopButtonListener)

        binding.toSecondActivity.setOnClickListener(toSecondActivityButtonListener)
    }

    override fun onResume() {
        super.onResume()

        Log.d("myTag", "onResume IS_PLAYING is ${IS_PLAYING}")

        isPlaying = IS_PLAYING ?: false
        IS_PLAYING = null
        updatePlayOrStopButton()
    }

    private val playOrStopButtonListener = { view: View ->
        sendBroadcast(Intent().apply {
            action = BROADCAST_CONSTANTS.ID.value
            putExtra(BROADCAST_CONSTANTS.STRING_DATA_TYPE_KEY.value, BROADCAST_CONSTANTS.SWITCH_STATE.value)
        })

        isPlaying = !isPlaying
        updatePlayOrStopButton()
    }

    private val toSecondActivityButtonListener = { view: View ->
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(BROADCAST_CONSTANTS.STRING_DATA_IS_PLAYING_KEY.value, isPlaying)
        })
    }

    private fun updatePlayOrStopButton() {
        if (isPlaying)
            binding.playOrStop.setImageResource(R.drawable.pause)
        else
            binding.playOrStop.setImageResource(R.drawable.play)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("myTag", "onDestroy")

        MediaPlayerService.stopService(applicationContext)
    }

    companion object {
        const val TAG = "myTag"

        var IS_PLAYING: Boolean? = null

        const val TRACK_NAME = "DU_HAST.mp3"
    }
}