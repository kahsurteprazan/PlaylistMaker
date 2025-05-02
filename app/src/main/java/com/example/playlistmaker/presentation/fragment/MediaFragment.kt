package com.example.playlistmaker.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"

        fun newInstance(tabType: String) = MediaFragment().apply {
            arguments = bundleOf(ARG_TAB_TYPE to tabType)
        }
    }

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        setupUI(arguments?.getString("tab_type"))
        return binding.root
    }

    private fun setupUI(tabType: String?) {
        when (tabType) {
            "playlists" -> {
                binding.button.visibility = View.VISIBLE
                binding.text.text = "Вы не создали\nни одного плейлиста"
            }
            else -> {
                binding.text.text = "Ваша медиа пуста"
                binding.button.visibility = View.GONE
            }
        }
    }
}