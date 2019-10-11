package com.rockspin.rxfluxcore.cached

import com.rockspin.rxfluxcore.Reducer
import com.rockspin.rxfluxcore.State
import com.rockspin.rxfluxcore.Store
import io.reactivex.Single

/**
 * A cached store is a store that save every update to the view state. When the resulting observable is subscribed to
 * that view state is loaded from the view cache.
 *
 * @param viewStateCache a view cache that can save a View State.
 * @return an observable that emits each time a [VS] is updated
 */
class CachedStore<VS : State>(
    private val viewStateCache: ViewStateCache<VS>,
    reducer: Reducer<VS>,
    initialState: Single<VS>
) : Store<VS>(reducer, initialState) {

    override fun sideEffect(it: VS) {
        viewStateCache.save(it)
    }

}

interface ViewStateCache<VS : State> {
    fun save(viewState: VS)
    fun loadViewState(): VS
}