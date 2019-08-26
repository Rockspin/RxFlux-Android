package com.rockspin.flux.service

import com.rockspin.flux.ui.*
import com.rockspin.rxfluxandroid.FluxViewModel
import org.koin.dsl.module

val serviceModule = module {
    single { Server() }
    factory { UserDataFetcher(get()) }
}