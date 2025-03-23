package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.use_case.player.PlayAudioInteract
import com.example.playlistmaker.domain.use_case.player.StartAudioUseCase
import com.example.playlistmaker.domain.use_case.player.PauseAudioUseCase
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

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var timerTextView: TextView
    private lateinit var play: ImageButton

    private val handler = Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT
    private var url: String? = ""

    private val audioPlayer: AudioPlayerRepository by lazy { Creator.createAudioPlayer() }
    private val playAudioUseCase: PlayAudioInteract by lazy { Creator.providePlayAudioInteract(audioPlayer) }
    private lateinit var startAudioUseCase: StartAudioUseCase
    private lateinit var pauseAudioUseCase: PauseAudioUseCase

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = audioPlayer.getCurrentPosition()
                timerTextView.text = SimpleDateFormat(
                    "m:ss",
                    Locale.getDefault()
                ).format(currentPosition)
            }
            handler.postDelayed(this, 300)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAudioUseCase = Creator.provideStartAudioUseCase(audioPlayer)
        pauseAudioUseCase = Creator.providePauseAudioUseCase(audioPlayer)

        timerTextView = binding.trackTimePlayedAudioPlayer
        play = binding.btnPlayAudioPlayer

        val track: Track? =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TRACK_KEY, Track::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(TRACK_KEY)
            }

        play.setOnClickListener {
            playbackControl()
        }

        if (track != null) {
            binding.trackNameAudioPlayer.text = track.trackName
            binding.artistNameAudioPlayer.text = track.artistName
            binding.textViewRightDuration.text = formatTrackTime(track.trackTimeMillis)

            url = track.previewUrl.toString()

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

            preparePlayer()
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
        pauseAudioUseCase.execute()
        playerState = STATE_PAUSED
        updatePlayButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        audioPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pauseAudioUseCase.execute()
                playerState = STATE_PAUSED
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startAudioUseCase.execute()
                playerState = STATE_PLAYING
                handler.post(updateRunnable)
            }
        }
        updatePlayButton()
    }

    private fun preparePlayer() {
        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: аудиофайл недоступен", Toast.LENGTH_SHORT).show()
            return
        }

        playAudioUseCase.execute(
            url!!,
            onPrepared = {
                play.isEnabled = true
                playerState = STATE_PREPARED
                updatePlayButton()
            },
            onCompletion = {
                playerState = STATE_PAUSED
                updatePlayButton()
                timerTextView.text = "0:00"
            }
        )
    }

    private fun updatePlayButton() {
        when (playerState) {
            STATE_PLAYING -> play.setImageResource(R.drawable.ic_pause)
            STATE_PREPARED, STATE_PAUSED -> play.setImageResource(R.drawable.ic_play)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTrackTime(timeMillis: Long): String {
        val minutes = timeMillis / 1000 / 60
        val seconds = (timeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun formatYearFromDate(dateString: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return null
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        return yearFormat.format(date)
    }

    private fun Int.toPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}
