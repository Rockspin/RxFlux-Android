package com.rockspin.rxfluxandroid

import com.rockspin.rxfluxcore.*
import io.reactivex.Observable


interface FluxModel <T : Event, VS : State, E : Effect> {

    /**
     * last emitted state.
     */
    fun currentState(): VS

    /**
     *  @return an observable whichto emits the current state. Subscriptions are shared see [Store.updates]
     */
    val onStateUpdated: Observable<VS>

    /**
     * @return an observable which when subscribed to emits the current state.
     */
    val onEffect : Observable<E>
}