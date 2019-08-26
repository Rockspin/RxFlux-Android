package com.rockspin.flux.ui

import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.view.clicks
import com.rockspin.flux.R
import com.rockspin.rxfluxandroid.FluxActivity
import com.rockspin.rxfluxandroid.FluxViewModel
import com.rockspin.rxfluxcore.*
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : FluxActivity<MainEvents, MainState, MainEffects>() {

    override val viewModel: MainFlux by inject()

    override val events: Observable<MainEvents> by lazy<Observable<MainEvents>> {
         button.clicks().map { MainEvents.ButtonClicked }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dispatchResults()
        attachStateUpdates()
    }

    override fun stateChanged(viewState: MainState) {
        name.text = viewState.user?.name
        userId.text = viewState.user?.id

        progressBar.isInvisible = !viewState.loading
    }

}


