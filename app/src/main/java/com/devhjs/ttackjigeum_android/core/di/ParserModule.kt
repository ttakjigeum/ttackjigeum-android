package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.BuildConfig
import com.devhjs.ttackjigeum_android.data.parser.DefaultProductParser
import com.devhjs.ttackjigeum_android.data.parser.DevProductParser
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser
import org.koin.dsl.module

val parserModule = module {
    single<ProductParser> {
        if (BuildConfig.FLAVOR == "dev") {
            DevProductParser()
        } else {
            DefaultProductParser(get())
        }
    }
}
