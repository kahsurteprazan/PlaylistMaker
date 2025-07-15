package com.example.playlistmaker.presentation.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentInfoPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.presentation.ui.AudioPlayerActivity
import com.example.playlistmaker.presentation.util.DpToPx.toPx
import com.example.playlistmaker.presentation.viewmodel.infoPlaylist.InfoPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel


class InfoPlaylist : Fragment() {

    private val args: InfoPlaylistArgs by navArgs()

    private var _binding: FragmentInfoPlaylistBinding? = null
    private val binding: FragmentInfoPlaylistBinding
        get() = _binding ?: throw RuntimeException("FragmentInfoPlaylistBinding = null")

    private val viewModel: InfoPlaylistViewModel by viewModel()

    private lateinit var adapter: TrackAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupButton()
        observeViewModel()

        viewModel.loadPlaylist(args.playlistId)
        setupBottomSheet()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupBottomSheet() {
        val tracksSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        val settingsSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheetSetting)

        tracksSheetBehavior.apply {
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
            isFitToContents = false
            binding.setting.post {
                val desiredHeight = binding.root.bottom - binding.setting.bottom - 24.toPx()
                peekHeight = desiredHeight
                binding.playlistsBottomSheet.requestLayout()
            }
        }

        settingsSheetBehavior.apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
            binding.setting.post {
                val desiredHeight = binding.root.bottom - binding.name.bottom
                peekHeight = desiredHeight
                binding.playlistsBottomSheetSetting.requestLayout()
            }
        }
        settingsSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_EXPANDED ||
                        newState == BottomSheetBehavior.STATE_COLLAPSED) View.VISIBLE else View.GONE
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = ((slideOffset.coerceIn(-1f, 1f) + 1) / 2)
            }
        })
    }


    private fun observeViewModel() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let { updatePlaylistInfo(it) }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.submitList(tracks)
            if (tracks.isEmpty()) {
                binding.placeholder.isVisible = true
                binding.recycler.isVisible = false
            } else {
                binding.placeholder.isVisible = false
                binding.recycler.isVisible = true
            }
        }


        viewModel.totalDuration.observe(viewLifecycleOwner) { duration ->
            binding.minCount.text = duration
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { showError(it) }
        }

        viewModel.deleteStatus.observe(viewLifecycleOwner) { success ->
            success?.let {
                if (it) {
                    Toast.makeText(context, "Плейлист удален", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show()
                }
                viewModel.clearDeleteStatus()
            }
        }
    }

    private fun updatePlaylistInfo(playlist: Playlist) {
        with(binding) {
            name.text = playlist.name
            description.text = playlist.description ?: ""
            trackCount.text = resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.trackCount,
                playlist.trackCount
            )

            loadPlaylistCover(playlist.coverImagePath)

            //<-- BottomSheet -->

            namePlaylist.text = playlist.name
            trackCount1.text = resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.trackCount,
                playlist.trackCount
            )
            if (!playlist.coverImagePath.isNullOrBlank()) {
                Glide.with(requireContext())
                    .load(playlist.coverImagePath)
                    .placeholder(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(binding.imgView)
            } else {
                binding.img.setImageResource(R.drawable.placeholder_image)
            }
        }
    }

    private fun loadPlaylistCover(coverPath: String?) {
        if (!coverPath.isNullOrBlank()) {
            Glide.with(requireContext())
                .load(coverPath)
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .into(binding.imgView)
        } else {
            binding.imgView.setImageResource(R.drawable.placeholder_image)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun setupRecycler() {
        adapter = TrackAdapter(
            onItemClickListener = { track -> onTrackClick(track) },
            onAddToHistoryClickListener = { track -> /* ничего */ },
            onTrackLongClick = { track -> onTrackLongClick(track) }
        )

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@InfoPlaylist.adapter
        }
    }


    private fun setupButton() {

        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        binding.share.setOnClickListener   { sharePlaylist() }
        binding.shareSheet.setOnClickListener {
            val playlist = viewModel.playlist.value
            val tracks = viewModel.tracks.value?.toList() ?: emptyList()

            if (playlist != null && tracks.isNotEmpty()) {
                sharePlaylist()
            } else {
                BottomSheetBehavior.from(binding.playlistsBottomSheetSetting).state = BottomSheetBehavior.STATE_HIDDEN

                Toast.makeText(requireContext(), "В лейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_SHORT).show()
            } }

        binding.edit.setOnClickListener {
            viewModel.playlist.value?.let { navigateToEditPlaylist(it.id) }
        }

        binding.delet.setOnClickListener {
            viewModel.playlist.value?.let { showDeleteConfirmationDialog(it.name) }
        }

        binding.setting.setOnClickListener {
            BottomSheetBehavior.from(binding.playlistsBottomSheetSetting).state  = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.overlay.setOnClickListener { BottomSheetBehavior.from(binding.playlistsBottomSheetSetting).state  = BottomSheetBehavior.STATE_HIDDEN }
    }

    private fun navigateToEditPlaylist(playlistId: Long) {
        val direction = CreatePlayListFragmentDirections
            .actionToCreatePlaylist(playlistId)
        findNavController().navigate(direction)
    }

    private fun sharePlaylist() {
        val playlist = viewModel.playlist.value
        val tracks = viewModel.tracks.value?.toList()  ?: emptyList()
        if (playlist != null && tracks.isNotEmpty()) {
            viewModel.sharePlaylist(playlist, tracks, requireContext())
        } else {
            Toast.makeText(requireContext(), "Нельзя поделиться пустым плейлистом", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onTrackClick(track: Track) {
        startActivity(
            Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                putExtra(KEY_TRACK, TrackUi(track))
            }
        )
    }

    private fun onTrackLongClick(track: Track) {
        showDeleteDialog(track)
        observeViewModel()
    }

    private fun showDeleteDialog(track: Track) {
        AlertDialog.Builder(requireContext())
            .setTitle("Хотите удалить трек?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deleteTrack(args.playlistId, track.trackId)
            }
            .setNegativeButton("Нет", null)
            .create()
            .show()
    }

    private fun showDeleteConfirmationDialog(playlistName: String) {
        AlertDialog.Builder(requireContext())
            .setMessage("Хотите удалить плейлист «${playlistName}»?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deletePlaylist(args.playlistId)
            }
            .setNegativeButton("нет", null)
            .show()
    }

    companion object {
        const val KEY_TRACK = "track"
    }

}