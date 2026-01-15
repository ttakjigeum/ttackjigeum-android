package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.BuildConfig
import com.devhjs.ttackjigeum_android.data.repository.DevUserConfigRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.ProdUserConfigRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.mock.MockPriceHistoryRepositoryImpl
import com.devhjs.ttackjigeum_android.data.repository.mock.MockProductRepositoryImpl
import com.devhjs.ttackjigeum_android.domain.repository.PriceHistoryRepository
import com.devhjs.ttackjigeum_android.domain.repository.ProductRepository
import com.devhjs.ttackjigeum_android.domain.repository.RoomUserConfigRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ProductRepository> { MockProductRepositoryImpl() }
    single<PriceHistoryRepository> { MockPriceHistoryRepositoryImpl() }
    single<RoomUserConfigRepository> {
        if (BuildConfig.FLAVOR == "dev") {
            DevUserConfigRepositoryImpl(get())
        } else {
            ProdUserConfigRepositoryImpl(get())
        }
    }
}