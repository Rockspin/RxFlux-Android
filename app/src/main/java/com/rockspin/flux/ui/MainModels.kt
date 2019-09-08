package com.rockspin.flux.ui

import com.rockspin.flux.service.User
import com.rockspin.rxfluxcore.*

sealed class MainEvents : Event {
    data class EmailChanged(val email: String) : MainEvents(), Result
    data class PasswordChanged(val password: String) : MainEvents(), Result
    data class LoginClicked(val email: String, val password: String) : MainEvents()
}

sealed class MainEffects : Effect {
    object OpenLoggedIn: MainEffects(), Navigation
}

private sealed class MainResults : Result {
    object closeApp : MainResults()
}

data class MainState(
    val loading: Boolean = false,
    val emailError: String = "",
    val passwordError: String = "",
    val canSignIn: Boolean = false,
    val email: String = "",
    val password: String = ""
) : State