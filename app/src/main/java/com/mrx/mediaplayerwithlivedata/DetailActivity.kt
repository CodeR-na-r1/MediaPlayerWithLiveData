package com.mrx.mediaplayerwithlivedata

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mrx.mediaplayerwithlivedata.databinding.ActivityDetailBinding
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updatePlayOrStopButton()

        binding.progressBar.min = 0

        MyMediaPlayerService.getLiveDataDuration().observe(this) {
            durationCallback(it)
        }

        MyMediaPlayerService.getLiveDataNowPosition().observe(this) {
            positionTrackCallback(it)
        }

        binding.playOrStop.setOnClickListener(playOrStopButtonListener)
    }

    @SuppressLint("SetTextI18n")
    private fun durationCallback(value: Int) {
            binding.durationTrack.text = "${TimeUnit.MILLISECONDS.toMinutes(value.toLong())}" +
                    ":" +
                    "${TimeUnit.MILLISECONDS.toSeconds(value.toLong()) % 60}"
            binding.progressBar.max = value
    }

    @SuppressLint("SetTextI18n")
    private fun positionTrackCallback(value: Int) {
            binding.nowPositionTrack.text = "${TimeUnit.MILLISECONDS.toMinutes(value.toLong())}" +
                    ":" +
                    "${TimeUnit.MILLISECONDS.toSeconds(value.toLong()) % 60}"
            binding.progressBar.progress = value
    }

    private val playOrStopButtonListener = { view: View ->
        MyMediaPlayerService.isPlaying.value = MyMediaPlayerService.isPlaying.value != true

        updatePlayOrStopButton()
    }

    private fun updatePlayOrStopButton() {
        if (MyMediaPlayerService.isPlaying.value == true)
            binding.playOrStop.setImageResource(R.drawable.pause)
        else
            binding.playOrStop.setImageResource(R.drawable.play)
    }

    companion object {
        const val TAG = "myTag"
    }
}