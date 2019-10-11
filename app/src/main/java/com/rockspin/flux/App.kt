package com.rockspin.flux

import android.app.Application
import com.rockspin.flux.service.serviceModule
import com.rockspin.flux.ui.mainActivityModule
import com.rockspin.flux.ui.success.successModule
import com.rockspin.rxfluxcore.*
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

        Flux.DEBUG = true
        Flux.logger = object : FluxDebugger{
            override fun resultCreatorSubscribed(resultCreator: ResultCreator<*>) {
                Timber.d("Result Creator Subscribed $resultCreator")
            }

            override fun resultDispatched(result: Result) {
                Timber.d("Result Dispatched $result")
            }

            override fun storeInitialized(store: Store<*>, state: State) {
                Timber.d("Store Subscribed $store state: $state")
            }

            override fun storeUpdated(store: Store<*>, it: State) {
                Timber.d("Store Updated $store state: $it  ")
            }

        }
    }
}