package com.rockspin.rxfluxandroid

import androidx.lifecycle.LifecycleOwner
import com.rockspin.rxfluxcore.*
import com.rockspin.rxfluxcore.cached.ViewStateCache
import com.rockspin.rxfluxcore.cached.toCachedStore
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.autoDisposable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

abstract class FluxViewModel<T : Event, VS : State, E : Effect>(
    val store: Store<VS>,
    val effectStore: EffectStore<E>? = null,
    val resultCreator: ResultCreator<T>? = null
) : AutoDisposeViewModel() {

    constructor(
        reducer: Reducer<VS>,
        initialState: VS,
        effectMapper: EffectMapper<E>? = null,
        resultCreator: ResultCreator<T>? = null
    ) : this(reducer.toStore(Single.just(initialState)), effectMapper?.toStore(), resultCreator)

    constructor(
        reducer: Reducer<VS>,
        initialState: Single<VS>,
        effectMapper: EffectMapper<E>? = null,
        resultCreator: ResultCreator<T>? = null
    ) : this(reducer.toStore(initialState), effectMapper?.toStore(), resultCreator)

    constructor(
        reducer: Reducer<VS>,
        viewStateCache: ViewStateCache<VS>,
        effectMapper: EffectMapper<E>? = null,
        resultCreator: ResultCreator<T>? = null
    ) : this(reducer.toCachedStore(viewStateCache), effectMapper?.toStore(), resultCreator)

    init {
        // subscribe to store updates on creation, because the
        // store updates are using replayingShare(), this means the view state will
        // survive orientation changes.
        attachStateUpdates()
    }

    /**
     * Register this [FluxViewModel] to the stores updates.
     *
     * this is called in init as store updates are using replayingShare(), this means the view
     * state and subscription will survive orientation changes.
     *
     * The subscription is automatically disposed when the activity this [FluxViewModel] is attached to
     * is destroyed.
     */
    private fun attachStateUpdates(): Disposable =
        store.updates.`as`(AutoDispose.autoDisposable<VS>(this)).subscribe()

}