package com.rockspin.rxfluxcore.cached

import com.jakewharton.rx.replayingShare
import com.rockspin.rxfluxcore.Dispatcher
import com.rockspin.rxfluxcore.Reducer
import com.rockspin.rxfluxcore.State
import com.rockspin.rxfluxcore.Store
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * A cached store is a store that save every update to the view state. When the resulting observable is subscribed to
 * that view state is loaded from the view cache.
 *
 * @param viewStateCache a view cache that can save a View State.
 * @return an observable that emits each time a [VS] is updated
 */
class CachedStore<VS : State>(
    override val reducer: Reducer<VS>,
    val viewStateCache: ViewStateCache<VS>
) : Store<VS> {

    override val initialState: Single<VS> = Single.fromCallable {
        viewStateCache.loadViewState()
    }

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
        .observeOn(Schedulers.io())
        .doOnNext { viewStateCache.save(it) }
        .replayingShare()

}

interface ViewStateCache<VS : State> {
    fun save(viewState: VS)
    fun loadViewState(): VS
}

fun <VS: State> Reducer<VS>.toCachedStore(viewStateCache: ViewStateCache<VS>): Store<VS> =
    CachedStore(this, viewStateCache)