package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.domain.usecase.GetPriceHistoryUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductUseCase
import com.devhjs.ttackjigeum_android.domain.usecase.GetProductsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetProductUseCase(get()) }
    single { GetProductsUseCase(get(), get()) }
    single { GetPriceHistoryUseCase(get()) }
}
