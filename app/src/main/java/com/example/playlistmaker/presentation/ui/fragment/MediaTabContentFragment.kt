package com.example.playlistmaker.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaContentBinding
import com.example.playlistmaker.presentation.entity.MediaTabType

class MediaTabContentFragment : Fragment() {

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"

        fun newInstance(tabType: MediaTabType): MediaTabContentFragment {
            return MediaTabContentFragment().apply {
                arguments = bundleOf(ARG_TAB_TYPE to tabType.name)
            }
        }
    }

    private var _binding: FragmentMediaContentBinding? = null
    private val binding get() = _binding ?: throw RuntimeException("FragmentMediaContentBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaContentBinding.inflate(inflater, container, false)

        val tabType = MediaTabType.valueOf(arguments?.getString(ARG_TAB_TYPE) ?: MediaTabType.PLAYLISTS.name)
        setupUI(tabType)

        return binding.root
    }

    private fun setupUI(tabType: MediaTabType) {
        when (tabType) {
            MediaTabType.PLAYLISTS -> {
                binding.button.visibility = View.VISIBLE
                binding.text.text = getString(R.string.media_playlist_zaglushka)
            }
            MediaTabType.FAVORITES -> {
                binding.text.text = getString(R.string.media_favorite_zaglushka)
                binding.button.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

