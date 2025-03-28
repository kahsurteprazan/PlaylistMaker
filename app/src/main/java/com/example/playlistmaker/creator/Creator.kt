package com.example.playlistmaker.creator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.ContactSupportRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.data.repository.ShareAppRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.data.repository.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.domain.use_case.player.PauseAudioUseCase
import com.example.playlistmaker.domain.use_case.player.PlayAudioInteract
import com.example.playlistmaker.domain.use_case.player.StartAudioUseCase
import com.example.playlistmaker.domain.use_case.search.AddTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.ClearHistoryUseCase
import com.example.playlistmaker.domain.use_case.setting.ContactSupportUseCase
import com.example.playlistmaker.domain.use_case.search.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SaveHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SearchTracksUseCase
import com.example.playlistmaker.domain.use_case.setting.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.setting.ThemeInteract

object Creator {

    private lateinit var application: Application

    private lateinit var appContext: Context

    private const val KEY_SEARCH_HISTORY = "search_history"

    fun initApplication(application: Application) {
        this.application = application
        appContext = application.applicationContext

    }

    private val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(appContext)
    }

    private val themeUseCase: ThemeInteract by lazy {
        ThemeInteract(themeRepository)
    }

    fun createAudioPlayer(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(MediaPlayer())
    }

    fun providePlayAudioInteract(audioPlayer: AudioPlayerRepository): PlayAudioInteract {
        return PlayAudioInteract(audioPlayer)
    }

    fun provideStartAudioUseCase(audioPlayer: AudioPlayerRepository): StartAudioUseCase {
        return StartAudioUseCase(audioPlayer)
    }

    fun providePauseAudioUseCase(audioPlayer: AudioPlayerRepository): PauseAudioUseCase {
        return PauseAudioUseCase(audioPlayer)
    }


    fun provideThemeUseCase(): ThemeInteract = themeUseCase

    fun provideContactSupportUseCase(activity: Activity): ContactSupportUseCase {
        val repository = ContactSupportRepositoryImpl(activity)
        return ContactSupportUseCase(repository)
    }

    fun provideShareAppUseCase(activity: Activity): ShareAppUseCase {
        val repository = ShareAppRepositoryImpl(activity)
        return ShareAppUseCase(repository)
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(KEY_SEARCH_HISTORY, Context.MODE_PRIVATE)
    }


    private fun provideTrackHistoryRepository(): TrackHistoryRepository {
        val sharedPreferences = provideSharedPreferences()
        return TrackHistoryRepositoryImpl(sharedPreferences)
    }


    fun provideAddTrackToHistoryUseCase(): AddTrackToHistoryUseCase {
        val repository = provideTrackHistoryRepository()
        return AddTrackToHistoryUseCase(repository)
    }

    fun provideGetHistoryUseCase(): GetHistoryUseCase {
        val repository = provideTrackHistoryRepository()
        return GetHistoryUseCase(repository)
    }

    fun provideClearHistoryUseCase(): ClearHistoryUseCase {
        val repository = provideTrackHistoryRepository()
        return ClearHistoryUseCase(repository)
    }

    fun provideSaveHistoryUseCase(): SaveHistoryUseCase {
        val repository = provideTrackHistoryRepository()
        return SaveHistoryUseCase(repository)
    }

    private fun provideSearchRepository(): SearchRepository {
        return SearchRepositoryImpl()
    }

    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        val repository = provideSearchRepository()
        return SearchTracksUseCase(repository)
    }

}