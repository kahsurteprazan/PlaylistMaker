package com.example.playlistmaker.di

import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.PlaylistDbConverter
import com.example.playlistmaker.data.db.PlaylistTrackDbConverter
import com.example.playlistmaker.data.db.TrackDbConverter
import com.example.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.data.repository.ShareAppRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.data.repository.TrackHistoryRepositoryImpl
import com.example.playlistmaker.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.repository.ShareAppRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.domain.repository.TrackHistoryRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get(named("app_context"))) }
    single<TrackHistoryRepository> { TrackHistoryRepositoryImpl(get(named("search_history"))) }
    single<SearchRepository> { SearchRepositoryImpl(trackDao = get()) }
    factory<MediaPlayer> { MediaPlayer() }
    single<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }

    single<ShareAppRepository> { ShareAppRepositoryImpl(get()) }
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    single { get<AppDatabase>().trackDao() }

    single { TrackDbConverter() }

    single { PlaylistTrackDbConverter() }

    single { PlaylistDbConverter() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            appDatabase = get(),
            converter = get(),
            converterForTrack = get()
        )
    }
}