package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.BuildConfig
import com.devhjs.ttackjigeum_android.data.repository.DevPriceHistoryRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.DevProductRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.DevUserConfigRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.FirestorePriceHistoryRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.FirestoreProductRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.FirestoreUserConfigRepositoryImpl
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.UserConfigRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ProductRepository> {
        if (BuildConfig.FLAVOR == "dev") {
            DevProductRepositoryImpl()
        } else {
            FirestoreProductRepositoryImpl(get())
        }
    }
    single<PriceHistoryRepository> {
        if (BuildConfig.FLAVOR == "dev") {
            DevPriceHistoryRepositoryImpl()
        } else {
            FirestorePriceHistoryRepositoryImpl(get())
        }
    }
    single<UserConfigRepository> {
        if (BuildConfig.FLAVOR == "dev") {
            DevUserConfigRepositoryImpl(get())
        } else {
            FirestoreUserConfigRepositoryImpl(get())
        }
    }
}