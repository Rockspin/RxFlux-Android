package com.rockspin.rxfluxcore

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * A [Store] is what holds the data of an application. A stores register with the [Dispatcher] so that they can receive
 * [Result]s .The data in a store must only be mutated by responding to an [Result].
 *
 * A [Store] only caches view state when someone is subscribed to the observable returned from [updates]
 *
 */
interface Store<VS : State> {
    fun currentViewState(): VS

    val updates: Observable<VS>
}

/**
 * A [Store] is what holds the data of an application. A stores register with the [Dispatcher] so that they can receive
 * [Result]s .The data in a store must only be mutated by responding to an [Result].
 *
 * A [Store] only caches view state when someone is subscribed to the observable returned from [updates]
 *
 */
open class BasicStore<VS : State>(
    private val reducer: Reducer<VS>,
    private val initialState: Single<VS>
) : Store<VS> {

    private lateinit var currentViewState: VS

    override fun currentViewState(): VS = currentViewState

    /**
     * emits updates to the store, should cache the result between subscriptions.
     * (see [com.jakewharton.rx.ReplayingShare])
     */
    override val updates: Observable<VS> = initialState
        .flatMapObservable { state ->
            Dispatcher.results.scan(state) { oldState, result ->
                reducer.reduceToState(
                    oldState,
                    result
                )
            }
        }
        .distinctUntilChanged()
        .doOnNext { currentViewState = it }
        .doOnNext { sideEffect(it) }
        .replayingShare()


    open fun sideEffect(it: VS) {}
}

fun <VS : State> Reducer<VS>.toStore(initialState: Single<VS>): Store<VS> =
    BasicStore(this, initialState)

fun <VS : State> Reducer<VS>.toStore(initialState: VS): Store<VS> =
    BasicStore(this, Single.just(initialState))

/**
 * Utility for combining stores into one object
 */
fun <VS : State, VS1 : State> Store<VS>.withStore(
    store: Store<VS1>,
    mapper: BiFunction<VS, VS1, VS>
): Store<VS> {

    return object : Store<VS> {
        private lateinit var currentViewState: VS

        override val updates: Observable<VS>
            get() =
                Observable.combineLatest(
                    this@withStore.updates,
                    store.updates,
                    mapper
                ).doOnNext { currentViewState = it }
                    .replayingShare()

        override fun currentViewState(): VS = currentViewState

    }
}