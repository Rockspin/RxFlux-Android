package com.rockspin.flux.ui

import com.jakewharton.rx.replayingShare
import com.rockspin.flux.service.LoginResult
import com.rockspin.flux.service.UserDataFetcher
import com.rockspin.flux.validate.EmailValidResult
import com.rockspin.flux.validate.PasswordValidResult
import com.rockspin.flux.validate.isEmailValid
import com.rockspin.flux.validate.isValidPassword
import com.rockspin.rxfluxandroid.FluxViewModel
import com.rockspin.rxfluxcore.*
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.merge
import io.reactivex.rxkotlin.ofType
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


class MainFlux(reducer: MainReducer, effectMapper: MainEffectMapper,  resultCreator: MainResultCreator) :
    FluxViewModel<MainEvents, MainState, MainEffects>(
        reducer,
        MainState(),
        effectMapper,
        resultCreator
    )


private data class CanLogin(
    val canLogin: Boolean
) : Result

class MainResultCreator(private val userDataFetcher: UserDataFetcher) : ResultCreator<MainEvents> {

    override fun createResults(events: Observable<MainEvents>): Observable<Result> {
        val passwordChanged = events.ofType<MainEvents.PasswordChanged>()
        val emailChanged = events.ofType<MainEvents.EmailChanged>()
        val loginClicked = events.ofType<MainEvents.LoginClicked>()

        val isPasswordValid = passwordChanged.map { it.password.isValidPassword() }.share()
        val isEmailValid = emailChanged.map { it.email.isEmailValid() }.share()

        val canLogin = Observables.combineLatest(
            isPasswordValid,
            isEmailValid
        ) { passwordValid: PasswordValidResult, emailValid ->
            passwordValid is PasswordValidResult.Valid && emailValid is EmailValidResult.Valid
        }.map { CanLogin(it) }

        val loginUser = loginClicked.flatMap { userDataFetcher.login(it.email, it.password) }

        return listOf(
            emailChanged,
            passwordChanged,
            isPasswordValid,
            isEmailValid,
            canLogin,
            loginUser
        ).merge()
    }
}

class MainReducer : Reducer<MainState> {
    override fun reduceToState(oldState: MainState, result: Result): MainState {

        return when (result) {
            is LoginResult -> {
                when (result) {
                    LoginResult.Loading -> oldState.copy(loading = true)
                    is LoginResult.Error -> oldState.copy(loading = false)
                    is LoginResult.Success -> oldState.copy(loading = false)
                }
            }
            is EmailValidResult -> {
                when (result) {
                    EmailValidResult.Valid -> oldState.copy(emailError = "")
                    EmailValidResult.TooShort -> oldState.copy(emailError = "Email Too short :(")
                    EmailValidResult.BadlyFormatted -> oldState.copy(emailError = "Email badly formatted Short :(")
                }
            }
            is PasswordValidResult -> {
                when (result) {
                    PasswordValidResult.Valid -> oldState.copy(passwordError = "")
                    PasswordValidResult.TooShort -> oldState.copy(passwordError = "Password too short :(")
                }
            }
            is MainEvents.EmailChanged -> oldState.copy(email = result.email)
            is MainEvents.PasswordChanged -> oldState.copy(password = result.password)
            is CanLogin -> oldState.copy(canSignIn = result.canLogin)
            else -> oldState
        }
    }
}


class MainEffectMapper : EffectMapper<MainEffects> {

    override fun mapToEffect(result: Result): MainEffects? {
        return when(result) {
            is LoginResult.Success -> MainEffects.OpenLoggedIn
            else -> null
        }
    }

}

val mainActivityModule = module {
    viewModel { MainFlux(get(), get(), get()) }
    factory { MainEffectMapper() }
    factory { MainResultCreator(get()) }
    factory { MainReducer() }
}

