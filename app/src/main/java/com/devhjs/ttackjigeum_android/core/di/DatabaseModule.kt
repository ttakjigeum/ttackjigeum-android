package com.devhjs.ttackjigeum_android.core.di

import androidx.room.Room
import com.devhjs.ttackjigeum_android.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "ttackjigeum.db",
        ).build()
    }
    single { get<AppDatabase>().userConfigDao() }
}