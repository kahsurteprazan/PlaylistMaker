package com.example.playlistmaker.di

import android.app.Activity
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.presentation.viewmodel.audioPlayer.AudioPlayerViewModel
import com.example.playlistmaker.presentation.viewmodel.media.MediaViewModel
import com.example.playlistmaker.presentation.viewmodel.search.SearchViewModel
import com.example.playlistmaker.presentation.viewmodel.setting.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (audioPlayer: AudioPlayerRepository) ->
        AudioPlayerViewModel(
            likedTracksInteract = get(),
            playAudioUseCase = get(),
            startAudioUseCase = get(),
            pauseAudioUseCase = get(),
            audioPlayer = audioPlayer
        )
    }

    viewModel {
        MediaViewModel(
            likedTracksInteract = get()
        )
    }

    viewModel {
        SearchViewModel(
            searchTracksUseCase = get(),
            addTrackToHistoryUseCase = get(),
            getHistoryUseCase = get(),
            clearHistoryUseCase = get()
        )
    }

    viewModel { (activity: Activity) ->
        SettingsViewModel(
            themeInteract = get(),
            shareAppUseCase = get(parameters = { parametersOf(activity) }),
            contactSupportUseCase = get(parameters = { parametersOf(activity) })
        )
    }
}