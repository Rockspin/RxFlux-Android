package com.rockspin.rxfluxcore

import io.reactivex.Observable

/**
 * An [EffectMapper] is a function that maps from [Result]s to [Effect]s
 */
interface EffectMapper<E : Effect> {

    fun mapToEffects(result: Result): Observable<E>

    fun emitEffects(): Observable<E> = Dispatcher.results.flatMap { mapToEffects(it) }
}


interface SimpleEffectMapper<E : Effect>: EffectMapper<E> {

    fun mapToEffect(result: Result): E?

    override fun mapToEffects(result: Result): Observable<E> =
        mapToEffect(result)?.let { Observable.just(it) } ?: Observable.empty()
}


fun <E : Effect> emptyEffectMapper() = object : EffectMapper<E> {
    override fun mapToEffects(result: Result): Observable<E> = Observable.empty()
}