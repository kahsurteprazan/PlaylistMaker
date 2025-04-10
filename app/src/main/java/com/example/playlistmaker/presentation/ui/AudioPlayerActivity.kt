package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.presentation.mappers.TrackMapper
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.presentation.viewmodel.audioPlayer.AudioPlayerViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val TRACK_KEY = "track"
    }

    private val audioPlayer: AudioPlayerRepository by inject()
    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(audioPlayer) }

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var timerTextView: TextView
    private lateinit var play: ImageButton

    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (viewModel.playerState.value == AudioPlayerViewModel.STATE_PLAYING) {
                val currentPosition = viewModel.getCurrentPosition()
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
        initViews()
        setupObservers()
        handleIntent()
        setupClickListeners()
    }

    private fun initViews() {
        timerTextView = binding.trackTimePlayedAudioPlayer
        play = binding.btnPlayAudioPlayer
    }

    private fun setupObservers() {
        viewModel.playButtonResId.observe(this) { resId ->
            play.setImageResource(resId)
        }

        viewModel.playerState.observe(this) { state ->
            when (state) {
                AudioPlayerViewModel.STATE_PLAYING -> handler.post(updateRunnable)
                else -> handler.removeCallbacks(updateRunnable)
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleIntent() {
        val trackUi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_KEY)
        } ?: return finish()
        val track = TrackMapper.mapToDomain(trackUi)

        track.let { setupTrackInfo(it) }
    }

    private fun setupTrackInfo(track: Track) {
        with(binding) {
            trackNameAudioPlayer.text = track.trackName
            artistNameAudioPlayer.text = track.artistName
            textViewRightDuration.text = formatTrackTime(track.trackTimeMillis)

            if (track.collectionName.isNotEmpty()) {
                textViewRightAlbum.text = track.collectionName
            } else {
                textViewRightAlbum.isGone = true
                textViewLeftAlbum.isGone = true
            }

            textViewRightYear.text = formatYearFromDate(track.releaseDate)
            textViewRightGenre.text = track.primaryGenreName
            textViewRightCountry.text = track.country

            Glide.with(this@AudioPlayerActivity)
                .load(track.getCoverArtwork())
                .transform(RoundedCorners(8.toPx()))
                .fitCenter()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(trackArtAudioPlayer)

            viewModel.preparePlayer(track.previewUrl.toString())
        }
    }

    private fun setupClickListeners() {
        play.setOnClickListener { viewModel.playbackControl() }
        binding.btnBackAudioPlayer.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
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
