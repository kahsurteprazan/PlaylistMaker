package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(named("app_context")) { androidContext() }
    single<SharedPreferences> {
        get<Context>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    single(named("search_history")) {
        get<Context>().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }
}