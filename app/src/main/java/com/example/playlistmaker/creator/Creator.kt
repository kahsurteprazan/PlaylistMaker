package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.data.repository.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.domain.use_case.AddTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.ClearHistoryUseCase
import com.example.playlistmaker.domain.use_case.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.SaveHistoryUseCase
import com.example.playlistmaker.domain.use_case.SearchTracksUseCase

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences("search_history", Context.MODE_PRIVATE)
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