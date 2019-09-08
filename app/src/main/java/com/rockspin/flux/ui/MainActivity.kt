package com.rockspin.flux.ui

import android.os.Bundle
import androidx.core.view.isInvisible
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.rockspin.flux.R
import com.rockspin.rxfluxandroid.FluxActivity
import com.rockspin.rxfluxandroid.rxSetText
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : FluxActivity<MainEvents, MainState, MainEffects>() {

    override val fluxViewModel: MainFlux by viewModel()

    override val events: Observable<MainEvents> by lazy {
        val debounceTextEntry: Long = 300
        listOf(
            signInButton.clicks().map {
                MainEvents.LoginClicked(
                    emailEntry.text.toString(),
                    passwordEntry.text.toString()
                )
            },
            emailEntry.textChanges()
                .skipInitialValue()
                .debounce(debounceTextEntry, TimeUnit.MILLISECONDS)
                .map { MainEvents.EmailChanged(it.toString()) },
            passwordEntry.textChanges()
                .skipInitialValue()
                .debounce(debounceTextEntry, TimeUnit.MILLISECONDS)
                .map { MainEvents.PasswordChanged(it.toString()) }
        ).merge()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setContentView(R.layout.activity_main)
    }

    override fun stateChanged(viewState: MainState) {
        emailEntry.rxSetText(viewState.email)
        passwordEntry.rxSetText(viewState.password)

        emailEntryContainer.error = viewState.emailError
        passwordEntryContainer.error = viewState.passwordError

        signInButton.isEnabled = viewState.canSignIn
        signInButton.isInvisible = viewState.loading
        progressBar.isInvisible = !viewState.loading
    }

    override fun receivedEffect(effect: MainEffects) {
        when (effect) {
            MainEffects.OpenLoggedIn -> startActivity(LoggedInActivity.newIntent(this))
        }
    }

}


