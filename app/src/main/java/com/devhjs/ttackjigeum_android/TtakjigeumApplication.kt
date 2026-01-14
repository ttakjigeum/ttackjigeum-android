package com.devhjs.ttackjigeum_android

import android.app.Application
import com.devhjs.ttackjigeum_android.core.di.repositoryModule
import com.devhjs.ttackjigeum_android.core.di.useCaseModule
import com.devhjs.ttackjigeum_android.core.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TtakjigeumApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TtakjigeumApplication)
            modules(
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
