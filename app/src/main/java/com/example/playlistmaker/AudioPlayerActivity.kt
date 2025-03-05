package com.example.playlistmaker

import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale




class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TRACK_KEY = "track"
    }

    private val handler = Handler(Looper.getMainLooper())
    private var mediaPlayer = MediaPlayer()
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var timerTextView: TextView
    private lateinit var play: ImageButton
    private var playerState = STATE_DEFAULT
    private var url =
        "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/mzaf_4002676889906514534.plus.aac.p.m4a"
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                timerTextView.text = SimpleDateFormat("m:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, 300)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerTextView = binding.trackTimePlayedAudioPlayer
        play = binding.btnPlayAudioPlayer

        val track: Track? =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TRACK_KEY, Track::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(TRACK_KEY)
            }

        preparePlayer()

        play.setOnClickListener {
            playbackControl()
        }


        if (track != null) {
            binding.trackNameAudioPlayer.text = track.trackName
            binding.artistNameAudioPlayer.text = track.artistName
            binding.textViewRightDuration.text = formatTrackTime(track.trackTimeMillis)

            if (track.collectionName.isNotEmpty()) {
                binding.textViewRightAlbum.text = track.collectionName
            } else {
                binding.textViewRightAlbum.isGone = true
                binding.textViewLeftAlbum.isGone = true
            }

            binding.textViewRightYear.text = formatYearFromDate(track.releaseDate)
            binding.textViewRightGenre.text = track.primaryGenreName
            binding.textViewRightCountry.text = track.country


            Glide.with(this)
                .load(track.getCoverArtwork())
                .transform(RoundedCorners(8.toPx()))
                .fitCenter()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.trackArtAudioPlayer)
        } else {
            finish()
        }


        binding.btnBackAudioPlayer.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_pause)
        handler.post(updateRunnable)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.ic_play)
            timerTextView.text = "0:00"
            playerState = STATE_PREPARED
        }
    }

    private fun formatTrackTime(timeMillis: Long): String {
        val minutes = timeMillis / 1000 / 60
        val seconds = (timeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatYearFromDate(dateString: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return null
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        return yearFormat.format(date)
    }

    fun Int.toPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }


}