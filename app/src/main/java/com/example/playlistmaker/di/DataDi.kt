package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.data.repository.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get(named("app_context"))) }
    single<TrackHistoryRepository> { TrackHistoryRepositoryImpl(get(named("search_history"))) }
    single<SearchRepository> { SearchRepositoryImpl() }
    factory<MediaPlayer> { MediaPlayer() }
    single<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }
}