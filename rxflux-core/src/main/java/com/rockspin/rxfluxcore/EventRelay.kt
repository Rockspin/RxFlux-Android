package com.rockspin.rxfluxcore

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class EventRelay<T : Event> {

    private val eventsRelay = PublishRelay.create<T>()

    val events: Observable<T> = eventsRelay.hide()

    fun dispatch(result: T) = eventsRelay.accept(result)

}