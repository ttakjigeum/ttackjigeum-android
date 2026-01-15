package com.devhjs.ttackjigeum_android.core.di

import com.devhjs.ttackjigeum_android.data.parser.ProductParserImpl
import com.devhjs.ttackjigeum_android.domain.parser.ProductParser
import org.koin.dsl.module

val parserModule = module {
    single<ProductParser> { ProductParserImpl(get()) }
}
