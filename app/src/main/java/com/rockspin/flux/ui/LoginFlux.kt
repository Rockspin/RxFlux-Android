package com.rockspin.flux.ui

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


class LoginFlux(reducer: MainReducer, effectMapper: LoginEffectMapper, resultCreator: LoginResultCreator) :
    FluxViewModel<LoginEvents, LoginState, LoginEffects>(
        LoginState(),
        reducer,
        effectMapper,
        resultCreator
    )


private data class CanLogin(
    val canLogin: Boolean
) : Result

class LoginResultCreator(private val userDataFetcher: UserDataFetcher) : ResultCreator<LoginEvents> {

    override fun createResults(events: Observable<LoginEvents>): Observable<Result> {
        // get the events we care about
        val passwordChanged = events.ofType<LoginEvents.PasswordChanged>()
        val emailChanged = events.ofType<LoginEvents.EmailChanged>()
        val loginClicked = events.ofType<LoginEvents.LoginClicked>()

        // check that form data is valid
        val isPasswordValid = passwordChanged.map { it.password.isValidPassword() }.share()
        val isEmailValid = emailChanged.map { it.email.isEmailValid() }.share()

        // check if the login button should be enabled
        val canLogin = Observables.combineLatest(
            isPasswordValid,
            isEmailValid
        ) { passwordValid: PasswordValidResult, emailValid ->
            passwordValid is PasswordValidResult.Valid && emailValid is EmailValidResult.Valid
        }.map { CanLogin(it) }

        // perform the login
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

class MainReducer : Reducer<LoginState> {

    override fun reduceToState(oldState: LoginState, result: Result): LoginState {
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
                 EmailValidResult.Valid ->
                     oldState.copy(emailError = "")
                 EmailValidResult.TooShort ->
                     oldState.copy(
                         emailError = "Email Too short :("
                     )
                 EmailValidResult.BadlyFormatted ->
                     oldState.copy(
                         emailError = "Email badly formatted Short :("
                     )
             }
         }
         is PasswordValidResult -> {
             when (result) {
                 PasswordValidResult.Valid -> oldState.copy(passwordError = "")
                 PasswordValidResult.TooShort -> oldState.copy(passwordError = "Password too short :(")
             }
         }
         is LoginEvents.EmailChanged -> oldState.copy(email = result.email)
         is LoginEvents.PasswordChanged -> oldState.copy(password = result.password)
         is CanLogin -> oldState.copy(canSignIn = result.canLogin)
         else -> oldState
     }
    }
}


class LoginEffectMapper : SimpleEffectMapper<LoginEffects> {

    override fun mapToEffect(result: Result): LoginEffects? {
        return when(result) {
            is LoginResult.Success -> LoginEffects.OpenLoggedIn
            else -> null
        }
    }

}

val mainActivityModule = module {
    viewModel { LoginFlux(get(), get(), get()) }
    factory { LoginEffectMapper() }
    factory { LoginResultCreator(get()) }
    factory { MainReducer() }
}

