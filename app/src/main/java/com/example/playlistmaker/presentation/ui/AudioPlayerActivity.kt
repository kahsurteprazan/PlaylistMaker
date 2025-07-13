package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.result.AddToPlaylistResult
import com.example.playlistmaker.presentation.adapter.AudioPlaylistAdapter
import com.example.playlistmaker.presentation.mappers.TrackMapper
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.presentation.ui.fragment.CreatePlayListFragment
import com.example.playlistmaker.presentation.util.DpToPx.toPx
import com.example.playlistmaker.presentation.viewmodel.audioPlayer.AudioPlayerViewModel
import com.example.playlistmaker.presentation.viewmodel.audioPlayer.PlayerState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
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
    private lateinit var playButton: ImageButton

    private lateinit var playlistAdapter: AudioPlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playlistAdapter = AudioPlaylistAdapter { playlistId ->
            onPlaylistSelected(playlistId)
        }

        initViews()
        bottomSheetInit()
        setupObservers()
        handleIntent()
        setupClickListeners()
        initRecycler()
        viewModel.loadPlaylists()
        observePlaylist()
        observeAddTrackPlaylist()
    }

    private fun initViews() {
        timerTextView = binding.trackTimePlayedAudioPlayer
        playButton = binding.btnPlayAudioPlayer
    }

    private fun observePlaylist() {
        viewModel.playlists.observe(this) { playlists ->
            playlistAdapter.submitList(playlists)
        }
    }

    private fun observeAddTrackPlaylist() {
        lifecycleScope.launch {
            viewModel.addToPlaylistResult.collect { result ->
                when (result) {
                    is AddToPlaylistResult.Success ->
                        Toast.makeText(this@AudioPlayerActivity,"Трек добавлен в плейлист",Toast.LENGTH_SHORT).show()
                    is AddToPlaylistResult.AlreadyExists -> {
                        Toast.makeText(this@AudioPlayerActivity,"Трек уже в плейлисте",Toast.LENGTH_SHORT).show()
                    }
                    is AddToPlaylistResult.Error -> {
                    Toast.makeText(this@AudioPlayerActivity,"Ошибка: ${result.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun onPlaylistSelected(playlistId: Long) {
        viewModel.addTrackToPlaylist(playlistId)
    }

    private fun initRecycler() {
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@AudioPlayerActivity)
            adapter = playlistAdapter
            setHasFixedSize(true)
        }
    }

    private fun bottomSheetInit() {
        with(BottomSheetBehavior.from(binding.playlistsBottomSheet)) {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }

                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val alpha = (slideOffset.coerceIn(-1f, 1f) + 1) / 2
                    binding.overlay.alpha = alpha
                }
            })
        }

    }

    private fun setupObservers() {
        viewModel.playerState.observe(this) { state ->
            playButton.isEnabled = state.isPlayButtonEnabled
            playButton.setImageResource(
                if (state is PlayerState.Playing) R.drawable.ic_pause
                else R.drawable.ic_play
            )
            timerTextView.text = state.progress
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.currentTrack.observe(this) { track ->
            track?.let {
                binding.btnLikeAudioPlayer.setImageResource(
                    if (it.isFavorite) R.drawable.ic_like
                    else R.drawable.ic_unlike
                )
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

        viewModel.setTrack(track)
        setupTrackInfo(track)
        viewModel.preparePlayer(track.previewUrl.toString())
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
            btnLikeAudioPlayer.setImageResource(
                if (track.isFavorite) R.drawable.ic_like
                else R.drawable.ic_unlike
            )

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
        }
    }

    private fun setupClickListeners() {
        playButton.setOnClickListener { viewModel.playbackControl() }
        binding.btnBackAudioPlayer.setOnClickListener { finish() }
        binding.btnLikeAudioPlayer.setOnClickListener {
            try {
                viewModel.onFavoriteClicked()
            } catch (e: Exception) {
                Log.e("AudioPlayerUI", "Error calling VM", e)
            }
        }
        binding.btnAddAudioPlayer.setOnClickListener {
            BottomSheetBehavior.from(binding.playlistsBottomSheet).state =
                BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.button.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreatePlayListFragment())
                .addToBackStack("audio")
                .commit()
            BottomSheetBehavior.from(binding.playlistsBottomSheet).state =
                BottomSheetBehavior.STATE_HIDDEN

            binding.mainContainer.visibility = View.GONE
            binding.fragmentContainer.visibility = View.VISIBLE
        }
        binding.overlay.setOnClickListener {
            BottomSheetBehavior.from(binding.playlistsBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.pause()
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
}
