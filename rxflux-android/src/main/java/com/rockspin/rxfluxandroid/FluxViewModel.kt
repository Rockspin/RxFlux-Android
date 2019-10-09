package com.rockspin.rxfluxandroid

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.rockspin.rxfluxcore.*
import com.rockspin.rxfluxcore.cached.ViewStateCache
import com.rockspin.rxfluxcore.cached.toCachedStore
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.lang.IllegalStateException

abstract class FluxViewModel<T : Event, VS : State, E : Effect>(
    store: Store<VS>,
    effectMapper: EffectMapper<E> = emptyEffectMapper(),
    resultCreator: ResultCreator<T> = emptyResultCreator()
) : AutoDisposeViewModel() {

    constructor(
        initialState: VS,
        reducer: Reducer<VS> = emptyReducer(),
        effectMapper: EffectMapper<E> = emptyEffectMapper(),
        resultCreator: ResultCreator<T> = emptyResultCreator()
    ) : this(reducer.toStore(Single.just(initialState)), effectMapper, resultCreator)

    private val fluxModel = FluxModel(store, resultCreator, effectMapper)


    init {
        // subscribe to store updates on creation, because the
        // store updates are using replayingShare(), this means the view state will
        // survive orientation changes.
        attachStateUpdates()
    }

    fun events(lifecycleOwner: LifecycleOwner, events: () -> Observable<T>) {
        fluxModel.createResults(events())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(lifecycleOwner.scope()).subscribe()
    }

    fun state(lifecycleOwner: LifecycleOwner, state: (VS) -> Unit): Disposable =
        fluxModel.state
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(lifecycleOwner.scope()).subscribe { state(it) }

    fun effects(lifecycleOwner: LifecycleOwner, effects: (E) -> Unit) =
        fluxModel.effects
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(lifecycleOwner.scope())
            .subscribe { effects(it) }

    /**
     * Register this [FluxViewModel] to the stores updates.
     *
     * this is called in init as store updates are using replayingShare(), this means the view
     * state and subscription will survive orientation changes.
     *
     * The subscription is automatically disposed when the activity this [FluxViewModel] is attached to
     * is destroyed.
     */
    private fun attachStateUpdates(): Disposable = fluxModel.state.autoDisposable(this).subscribe()


}