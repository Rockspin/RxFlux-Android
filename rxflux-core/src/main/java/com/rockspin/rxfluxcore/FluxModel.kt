package com.rockspin.rxfluxcore

import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

open class FluxModel<T : Event, VS : State, E : Effect>(
    store: Store<VS>,
    private val resultCreator: ResultCreator<T> = emptyResultCreator(),
    effectMapper: EffectMapper<E> = emptyEffectMapper()
) {

    val state: Observable<VS> = store.updates.replayingShare()
    val effects: Observable<E> = effectMapper.emitEffects()

    fun createResults(events: Observable<T>): Observable<Result> =
        events.publish(resultCreator::createResults).doOnNext { Dispatcher.dispatch(it) }

}

open class EventRelay<T : Event> {

    private val eventsRelay = PublishRelay.create<T>()

    val events: Observable<T> = eventsRelay.hide()

    fun dispatch(result: T) = eventsRelay.accept(result)

}

abstract class StaticModel<T : Event, VS : State, E : Effect>(
    store: Store<VS>,
    effectMapper: EffectMapper<E> = emptyEffectMapper(),
    resultCreator: ResultCreator<T> = emptyResultCreator()
) : FluxModel<T, VS, E>(store, resultCreator, effectMapper) {

    private val eventDispatcher: EventRelay<T> = EventRelay()
    private var disposeable: CompositeDisposable? = null

    val results by lazy { createResults(eventDispatcher.events).replayingShare() }

    fun dispatch(event: T) {
        eventDispatcher.dispatch(event)
    }

    fun connect() {
        disposeable = CompositeDisposable(
            results.subscribe(),
            effects.subscribe(),
            state.subscribe()
        )
    }

    fun disconnect() {
        disposeable?.dispose()
    }

}

