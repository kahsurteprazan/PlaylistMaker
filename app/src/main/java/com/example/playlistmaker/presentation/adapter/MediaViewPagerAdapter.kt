package com.example.playlistmaker.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.presentation.entity.MediaTabType
import com.example.playlistmaker.presentation.ui.fragment.MediaTabContentFragment

class MediaViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MediaTabContentFragment.newInstance(MediaTabType.FAVORITES)
            1 -> MediaTabContentFragment.newInstance(MediaTabType.PLAYLISTS)
            else -> throw IllegalArgumentException()
        }
    }
}