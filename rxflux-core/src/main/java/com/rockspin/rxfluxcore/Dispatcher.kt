package com.rockspin.rxfluxcore

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

/**
 * The dispatcher receives [Result]s and dispatches them to [Store]s that have registered with the dispatcher.
 * Every store will receive every [Result]. There is only one singleton dispatcher in each application.
 */
object Dispatcher {

    private val resultsRelay =
        PublishRelay.create<Result>()

    val results: Observable<Result> = resultsRelay.hide()

    fun dispatch(result: Result) = resultsRelay.accept(result)
}