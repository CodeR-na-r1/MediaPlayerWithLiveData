package com.mrx.mediaplayerwithlivedata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mrx.mediaplayerwithlivedata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var counterTrack = 1
    private val trackList = arrayOf("DU_HAST.mp3", "Crazy_Frog_Axel_f.mp3", "Traktor.mp3")

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myTag", "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyMediaPlayerService.startService(applicationContext)
        MyMediaPlayerService.TRACK_NAME.value = trackList[1]

        binding.trackName.text = MyMediaPlayerService.TRACK_NAME.value

        binding.playOrStop.setOnClickListener(playOrStopButtonListener)

        binding.next.setOnClickListener(nextButtonListener)
        binding.prev.setOnClickListener(prevButtonListener)

        binding.toSecondActivity.setOnClickListener(toSecondActivityButtonListener)
    }

    private val playOrStopButtonListener = { view: View ->
        MyMediaPlayerService.isPlaying.value = MyMediaPlayerService.isPlaying.value != true

        updatePlayOrStopButton()
    }

    override fun onResume() {
        super.onResume()

        updatePlayOrStopButton()
    }

    private val nextButtonListener = { view: View ->
        if (++counterTrack >= trackList.size) { counterTrack = 0 }

        MyMediaPlayerService.TRACK_NAME.value = trackList[counterTrack]
        binding.trackName.text = MyMediaPlayerService.TRACK_NAME.value
    }

    private val prevButtonListener = { view: View ->
        if (--counterTrack < 0) { counterTrack = trackList.size - 1 }

        MyMediaPlayerService.TRACK_NAME.value = trackList[counterTrack]
        binding.trackName.text = MyMediaPlayerService.TRACK_NAME.value
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