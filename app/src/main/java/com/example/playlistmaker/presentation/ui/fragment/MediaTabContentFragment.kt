package com.example.playlistmaker.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaContentBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.entity.MediaTabType
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.presentation.ui.AudioPlayerActivity
import com.example.playlistmaker.presentation.viewmodel.media.MediaViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaTabContentFragment : Fragment() {

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"
        private const val KEY_TRACK = "track"

        fun newInstance(tabType: MediaTabType): MediaTabContentFragment {
            return MediaTabContentFragment().apply {
                arguments = bundleOf(ARG_TAB_TYPE to tabType.name)
            }
        }
    }

    private var _binding: FragmentMediaContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TrackAdapter
    private val viewModel: MediaViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabType = MediaTabType.valueOf(
            arguments?.getString(ARG_TAB_TYPE) ?: MediaTabType.PLAYLISTS.name
        )
        setupRecyclerView()
        if (tabType == MediaTabType.FAVORITES) {
            observeFavorites()
            setupFavoritesUI()
        } else {
            setupPlaylistsUI()
        }
    }

    private fun observeFavorites() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MediaViewModel.State.Initial -> {
                    binding.text.visibility = View.GONE
                    binding.imageView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    //binding.progressBar.visibility = View.VISIBLE
                }
                is MediaViewModel.State.Empty -> {
                    binding.text.visibility = View.VISIBLE
                    binding.imageView.visibility = View.VISIBLE
                    binding.text.text = getString(R.string.media_favorite_zaglushka)
                    binding.recyclerView.visibility = View.GONE
                }
                is MediaViewModel.State.Content -> {
                    binding.text.visibility = View.GONE
                    binding.imageView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.submitList(state.tracks)
                }
                is MediaViewModel.State.Error -> {
                    binding.text.visibility = View.VISIBLE
                    binding.imageView.visibility = View.VISIBLE
                    binding.text.text = getString(R.string.media_favorite_zaglushka)
                    binding.recyclerView.visibility = View.GONE
                }
            }
        }
    }

    private fun setupPlaylistsUI() {
        with(binding) {
            button.visibility = View.VISIBLE
            text.visibility = View.VISIBLE
            text.text = getString(R.string.media_playlist_zaglushka)
            recyclerView.visibility = View.GONE

            button.setOnClickListener {
                // Обработка создания плейлиста
            }
        }
    }

    private fun setupFavoritesUI() {
        with(binding) {
            button.visibility = View.GONE
            text.visibility = View.GONE
            imageView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(
            onItemClickListener = { track -> onTrackClick(track) },
            onAddToHistoryClickListener = { track -> /* ничего */ }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MediaTabContentFragment.adapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            setHasFixedSize(true)
        }
    }

    private fun onTrackClick(track: Track) {
        startActivity(
            Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                putExtra(KEY_TRACK, TrackUi(track))
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

