package com.rockspin.rxfluxcore

import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import io.reactivex.rxkotlin.ofType

/**
 * A [ResultCreator] is a function that maps from [Event]s to [Result]s
 */
interface ResultCreator<T : Event> {

    fun createResults(events: Observable<T>): Observable<Result>

    fun dispatcheResults(events: Observable<T>): Observable<Result> =
        events.doOnSubscribe {
            Flux.resultCreatorSubscribed(this)
                }
            .publish(this::createResults)
            .doOnNext { Dispatcher.dispatch(it) }
}


fun <T : Event> emptyResultCreator() = object : ResultCreator<T> {
    override fun createResults(events: Observable<T>): Observable<Result> = events.ofType()
}


fun <T : Event> combineResultCreators(vararg resultCreators: ResultCreator<T>): ResultCreator<T> {
    return object : ResultCreator<T> {
        override fun createResults(events: Observable<T>): Observable<Result> =
            resultCreators.map { it.createResults(events) }.toList().merge()
    }
}


