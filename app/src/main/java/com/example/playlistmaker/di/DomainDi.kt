package com.example.playlistmaker.di

import android.app.Activity
import com.example.playlistmaker.data.repository.ContactSupportRepositoryImpl
import com.example.playlistmaker.data.repository.LikedTracksRepositoryImpl
import com.example.playlistmaker.data.repository.ShareAppRepositoryImpl
import com.example.playlistmaker.domain.impl.LikedTracksInteractImpl
import com.example.playlistmaker.domain.repository.LikedTracksRepository
import com.example.playlistmaker.domain.use_case.media.LikedTracksInteract
import com.example.playlistmaker.domain.use_case.player.PauseAudioUseCase
import com.example.playlistmaker.domain.use_case.player.PlayAudioInteract
import com.example.playlistmaker.domain.use_case.player.StartAudioUseCase
import com.example.playlistmaker.domain.use_case.search.AddTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.ClearHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SaveHistoryUseCase
import com.example.playlistmaker.domain.use_case.search.SearchTracksUseCase
import com.example.playlistmaker.domain.use_case.setting.ContactSupportUseCase
import com.example.playlistmaker.domain.use_case.setting.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.setting.ThemeInteract
import org.koin.dsl.module

val domainModule = module {
    factory { PlayAudioInteract(get()) }
    factory { StartAudioUseCase(get()) }
    factory { PauseAudioUseCase(get()) }
    factory<SearchTracksUseCase> { SearchTracksUseCase(get()) }
    factory<AddTrackToHistoryUseCase> { AddTrackToHistoryUseCase(get()) }
    factory<GetHistoryUseCase> { GetHistoryUseCase(get()) }
    factory<ClearHistoryUseCase> { ClearHistoryUseCase(get()) }
    factory { SaveHistoryUseCase(get()) }
    factory { ThemeInteract(get()) }
    factory { (activity: Activity) -> ShareAppUseCase(ShareAppRepositoryImpl(activity)) }
    factory { (activity: Activity) -> ContactSupportUseCase(ContactSupportRepositoryImpl(activity)) }

    single<LikedTracksRepository> {
        LikedTracksRepositoryImpl(
            appDatabase = get(),
            converter = get()
        )
    }

    factory<LikedTracksInteract> {
        LikedTracksInteractImpl(
            likedTracksRepository = get()
        )
    }
}