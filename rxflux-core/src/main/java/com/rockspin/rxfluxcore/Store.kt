package com.rockspin.rxfluxcore

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
/**
 * A [Store] is what holds the data of an application. A stores register with the [Dispatcher] so that they can receive
 * [Result]s .The data in a store must only be mutated by responding to an [Result].
 *
 * A [Store] only caches view state when someone is subscribed to the observable returned from [updates]
 *
 */
open class Store<VS : State>(
    private val reducer: Reducer<VS>,
    private val initialState: Single<VS>
){
    lateinit var currentViewState: VS
        private set

    /**
     * emits updates to the store, should cache the result between subscriptions.
     * (see [com.jakewharton.rx.ReplayingShare])
     */
    val updates : Observable<VS> = initialState
        .flatMapObservable { state ->
            Flux.storeInitialized(this, state)

            Dispatcher.results.scan(state) { oldState, result ->
                reducer.reduceToState(
                    oldState,
                    result
                )
            }
        }
        .distinctUntilChanged()
        .doOnNext {
            currentViewState = it
            sideEffect(it)
        }
        .doOnNext {
            Flux.storeUpdated(this,it)
        }
        .replayingShare()


    open fun sideEffect(it: VS) {}
}

fun <VS : State> Reducer<VS>.toStore(initialState: Single<VS>): Store<VS> =
    Store(this, initialState)

fun <VS : State> Reducer<VS>.toStore(initialState: VS): Store<VS> =
    Store(this, Single.just(initialState))
