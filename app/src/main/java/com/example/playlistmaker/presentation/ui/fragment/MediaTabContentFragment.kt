package com.example.playlistmaker.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaContentBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.PlaylistAdapter
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.entity.MediaTabType
import com.example.playlistmaker.presentation.model.TrackUi
import com.example.playlistmaker.presentation.ui.AudioPlayerActivity
import com.example.playlistmaker.presentation.viewmodel.media.MediaViewModel
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
    private lateinit var adapterPlaylist: PlaylistAdapter
    private  val viewModel: MediaViewModel by viewModel()

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
        Log.d("MediaDebug", "Fragment создан: ${getCurrentTabType()}")
        setupRecyclerView()

        if (getCurrentTabType() == MediaTabType.FAVORITES) {
            binding.apply {
                favoritesStub.visibility = View.VISIBLE
                playlistStub.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

        } else {
            binding.apply {
                playlistStub.visibility = View.VISIBLE
                favoritesStub.visibility = View.GONE
            }
        }


        if (getCurrentTabType() == MediaTabType.FAVORITES) {
            Log.d("MediaDebug", "Загружаем избранные треки")
            observeFavorites()
            viewModel.loadLikedTracks()
        } else {
            viewModel.loadPlaylists()
            observePlaylist()
        }

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_to_create_playlist)
        }
    }



    private fun observeFavorites() {
        Log.d("MediaDebug", "Наблюдение за избранным начато")
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MediaViewModel.State.Initial -> showInit()
                is MediaViewModel.State.Empty -> showEmptyState()
                is MediaViewModel.State.Content -> showContent(state.tracks)
                is MediaViewModel.State.Error -> showEmptyState()
            }
        }
    }

    private fun setupFavoritesUI() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.favoritesStub.visibility = View.VISIBLE
        binding.playlistStub.visibility = View.GONE
        binding.imageView1.visibility = View.GONE
        binding.text1.visibility = View.GONE
    }

    private fun observePlaylist() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapterPlaylist.submitList(playlists)
            updatePlaylistsVisibility(playlists.isEmpty())
        }
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(

            onItemClickListener = { track -> onTrackClick(track) },
            onAddToHistoryClickListener = { track -> /* ничего */ }
        )
        adapterPlaylist = PlaylistAdapter()

        binding.recyclerViewPlaylist.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@MediaTabContentFragment.adapterPlaylist
            setHasFixedSize(true)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MediaTabContentFragment.adapter
            setHasFixedSize(true)
        }

    }

    private fun showInit() {
        setupFavoritesUI()
    }

    private fun getCurrentTabType(): MediaTabType {
        return MediaTabType.valueOf(
            arguments?.getString(ARG_TAB_TYPE) ?: MediaTabType.PLAYLISTS.name
        )
    }

    private fun showEmptyState() {
        val tabType = getCurrentTabType()

        if (tabType == MediaTabType.FAVORITES) {
            with(binding) {
                favoritesStub.visibility = View.VISIBLE
                playlistStub.visibility = View.GONE
                recyclerView.visibility = View.GONE
                imageView1.visibility = View.VISIBLE
                text1.visibility = View.VISIBLE
            }
        } else {
            with(binding) {
                playlistStub.visibility = View.VISIBLE
                favoritesStub.visibility = View.GONE
                recyclerViewPlaylist.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                text.visibility = View.VISIBLE
            }
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.apply {
            recyclerView.visibility = View.VISIBLE
            favoritesStub.visibility = View.VISIBLE
            playlistStub.visibility = View.GONE
            recyclerViewPlaylist.visibility = View.GONE
            imageView1.visibility = View.GONE
            text1.visibility = View.GONE
            imageView.visibility = View.GONE
            text.visibility = View.GONE
        }

        adapter.submitList(tracks)

        binding.recyclerView.post {
            binding.recyclerView.requestLayout()
        }
    }
    private fun updatePlaylistsVisibility(isEmpty: Boolean) {
        if (isEmpty) {
            binding.imageView.visibility = View.VISIBLE
            binding.text.visibility = View.VISIBLE
            binding.recyclerViewPlaylist.visibility = View.GONE
        } else {
            binding.imageView.visibility = View.GONE
            binding.text.visibility = View.GONE
            binding.recyclerViewPlaylist.visibility = View.VISIBLE
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

