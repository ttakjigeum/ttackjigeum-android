package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.presentation.detail.DetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (route: Long) -> DetailViewModel(route, get(), get()) }
}
