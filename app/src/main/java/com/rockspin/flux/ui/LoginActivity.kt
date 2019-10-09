package com.rockspin.flux.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.rockspin.flux.R
import com.rockspin.flux.ui.success.SuccessActivity
import com.rockspin.rxfluxandroid.rxSetText
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private val fluxViewModel: LoginFlux by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setContentView(R.layout.activity_login)

        fluxViewModel.events(this){
            // Emit LoginClicked event on click
            val onLoginClick = signInButton.clicks().map {
                LoginEvents.LoginClicked(
                    emailEntry.text.toString(),
                    passwordEntry.text.toString()
                )
            }

            // text entry is debounced to prevent lots of events
            val debounceTextEntry: Long = 300

            // emit EmailChanged event on text entry
            val onEmailChanged = emailEntry.textChanges()
                .skipInitialValue()
                .debounce(debounceTextEntry, TimeUnit.MILLISECONDS)
                .map { LoginEvents.EmailChanged(it.toString()) }

            // emit PasswordChanged event on text entry
            val onPasswordChanged = passwordEntry.textChanges()
                .skipInitialValue()
                .debounce(debounceTextEntry, TimeUnit.MILLISECONDS)
                .map { LoginEvents.PasswordChanged(it.toString()) }

            // merge all events in to single observable
            listOf(
                onLoginClick,
                onEmailChanged,
                onPasswordChanged
            ).merge()
        }

        fluxViewModel.state(this){ viewState ->
            emailEntry.rxSetText(viewState.email)
            passwordEntry.rxSetText(viewState.password)

            emailEntryContainer.error = viewState.emailError
            passwordEntryContainer.error = viewState.passwordError

            signInButton.isEnabled = viewState.canSignIn
            signInButton.isInvisible = viewState.loading
            progressBar.isInvisible = !viewState.loading
        }

        fluxViewModel.effects(this){
            when (it) {
                LoginEffects.OpenLoggedIn -> startActivity(SuccessActivity.newIntent(this))
            }
        }
    }

}


