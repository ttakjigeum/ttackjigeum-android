package com.devhjs.ttackjigeum_android.core.di

import androidx.room.Room
import com.devhjs.ttackjigeum_android.data.local.database.AppDatabase
import com.devhjs.ttackjigeum_android.data.repository.ProductRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.UserConfigRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.mock.MockPriceHistoryRepositoryImpl
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "ttackjigeum.db"
        ).build()
    }
    single { get<AppDatabase>().productDao() }
    single { get<AppDatabase>().userConfigDao() }

    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<UserConfigRepository> { UserConfigRepositoryImpl(get()) }
    single<PriceHistoryRepository> { MockPriceHistoryRepositoryImpl() }
}