package com.rockspin.flux

import android.app.Application
import com.rockspin.flux.service.serviceModule
import com.rockspin.flux.ui.mainActivityModule
import com.rockspin.flux.ui.success.successModule
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                listOf(
                    mainActivityModule,
                    serviceModule,
                    successModule
                )
            )
        }

        Timber.plant(Timber.DebugTree())
    }
}