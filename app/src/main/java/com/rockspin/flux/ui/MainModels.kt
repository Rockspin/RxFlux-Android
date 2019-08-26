package com.rockspin.flux.ui

import com.rockspin.flux.service.User
import com.rockspin.rxfluxcore.Effect
import com.rockspin.rxfluxcore.Event
import com.rockspin.rxfluxcore.Result
import com.rockspin.rxfluxcore.State

sealed class MainEvents : Event {
    object ButtonClicked : MainEvents()
}

sealed class MainEffects : Effect {
    object closeApp : MainEffects()
}

private sealed class MainResults : Result {
    object closeApp : MainResults()
}

data class MainState(
    val loading: Boolean = false,
    val user: User? = null
) : State