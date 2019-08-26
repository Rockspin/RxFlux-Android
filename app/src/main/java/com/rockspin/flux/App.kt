package com.rockspin.flux

import android.app.Application
import com.rockspin.flux.service.serviceModule
import com.rockspin.flux.ui.mainActivityModule
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                listOf(
                    mainActivityModule,
                    serviceModule
                )
            )
        }

        Timber.plant(Timber.DebugTree())
    }
}