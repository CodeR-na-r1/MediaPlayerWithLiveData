package com.mrx.mediaplayerwithlivedata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mrx.mediaplayerwithlivedata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTag", "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyMediaPlayerService.startService(applicationContext)
        MyMediaPlayerService.TRACK_NAME.value = "DU_HAST.mp3"

        binding.playOrStop.setOnClickListener(playOrStopButtonListener)

        binding.toSecondActivity.setOnClickListener(toSecondActivityButtonListener)
    }

    private val playOrStopButtonListener = { view: View ->
        MyMediaPlayerService.isPlaying.value = MyMediaPlayerService.isPlaying.value != true

        updatePlayOrStopButton()
    }

    private val toSecondActivityButtonListener = { view: View ->
        startActivity(Intent(this, DetailActivity::class.java))
    }

    private fun updatePlayOrStopButton() {
        if (MyMediaPlayerService.isPlaying.value == true)
            binding.playOrStop.setImageResource(R.drawable.pause)
        else
            binding.playOrStop.setImageResource(R.drawable.play)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("myTag", "onDestroy")

        MyMediaPlayerService.stopService(applicationContext)
    }

    companion object {
        const val TAG = "myTag"
    }
}