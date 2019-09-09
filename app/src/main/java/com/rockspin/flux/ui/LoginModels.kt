package com.rockspin.flux.ui

import com.rockspin.rxfluxcore.*

sealed class LoginEvents : Event {
    data class EmailChanged(val email: String) : LoginEvents(), Result
    data class PasswordChanged(val password: String) : LoginEvents(), Result
    data class LoginClicked(val email: String, val password: String) : LoginEvents()
}

data class LoginState(
    // Loading state.
    val loading: Boolean = false,
    // Form state.
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    // Button state.
    val canSignIn: Boolean = false
) : State

sealed class LoginEffects : Effect {
    object OpenLoggedIn: LoginEffects(), Navigation
}
