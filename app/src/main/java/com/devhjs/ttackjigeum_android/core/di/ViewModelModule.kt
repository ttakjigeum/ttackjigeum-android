package com.devhjs.ttackjigeum_android.core.di


import com.devhjs.ttackjigeum_android.presentation.detail.DetailViewModel
import com.devhjs.ttackjigeum_android.presentation.list.ListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { ListViewModel(get()) }
    viewModel { (route: Long) -> DetailViewModel(route, get(), get(),get(),get(), get()) }
}
