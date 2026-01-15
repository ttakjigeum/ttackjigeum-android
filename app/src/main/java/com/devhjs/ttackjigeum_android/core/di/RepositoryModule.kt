package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.BuildConfig
import com.devhjs.ttackjigeum_android.data.repository.DevPriceHistoryRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.DevProductRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.DevUserConfigRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.ProdPriceHistoryRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.ProdProductRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.ProdUserConfigRepositoryImpl
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ProductRepository> {
        if (BuildConfig.FLAVOR == "dev") {
            DevProductRepositoryImpl()
        } else {
            ProdProductRepositoryImpl()
        }
    }
    single<PriceHistoryRepository> {
        if (BuildConfig.FLAVOR == "dev") {
            DevPriceHistoryRepositoryImpl()
        } else {
            ProdPriceHistoryRepositoryImpl()
        }
    }
    single<RoomUserConfigRepository> {
        if (BuildConfig.FLAVOR == "dev") {
            DevUserConfigRepositoryImpl(get())
        } else {
            ProdUserConfigRepositoryImpl(get())
        }
    }
}