package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.core.manager.ClipboardStateManager
import org.koin.dsl.module

val managerModule = module {
    single { ClipboardStateManager() }
}
