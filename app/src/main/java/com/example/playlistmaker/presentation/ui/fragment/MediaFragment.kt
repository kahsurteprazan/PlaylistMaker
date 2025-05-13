package com.example.playlistmaker.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.presentation.adapter.MediaViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment(R.layout.fragment_media) {

    private lateinit var binding: FragmentMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMediaBinding.bind(view)

        binding.viewPager.adapter = MediaViewPagerAdapter(childFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Избранные треки"
                1 -> tab.text = "Плейлисты"
            }
        }
        tabMediator.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }
}