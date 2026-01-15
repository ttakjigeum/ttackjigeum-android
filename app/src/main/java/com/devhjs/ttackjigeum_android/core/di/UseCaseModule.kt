package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.domain.usecase.GetPriceHistoryUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductsUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetUserConfigUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.ToggleProductNotificationUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetProductUseCase(get()) }
    single { GetProductsUseCase(get(), get()) }
    single { GetPriceHistoryUseCase(get()) }
    single { GetUserConfigUseCase(get()) }
    single { ToggleProductNotificationUseCase(get()) }
}
