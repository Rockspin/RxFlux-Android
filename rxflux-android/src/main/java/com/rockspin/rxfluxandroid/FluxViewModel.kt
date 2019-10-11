package com.rockspin.rxfluxandroid

import androidx.lifecycle.LifecycleOwner
import com.rockspin.rxfluxcore.*
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class FluxViewModel<T : Event, VS : State, E : Effect>(
    private val store: Store<VS>,
    effectMapper: EffectMapper<E> = emptyEffectMapper(),
    private val resultCreator: ResultCreator<T> = emptyResultCreator()
) : AutoDisposeViewModel(), FluxModel<T, VS,E > {

    constructor(
        initialState: VS,
        reducer: Reducer<VS> = emptyReducer(),
        effectMapper: EffectMapper<E> = emptyEffectMapper(),
        resultCreator: ResultCreator<T> = emptyResultCreator()
    ) : this(reducer.toStore(Single.just(initialState)), effectMapper, resultCreator)

    override fun currentState(): VS = store.currentViewState

    override val onStateUpdated: Observable<VS> = store.updates

    override val onEffect : Observable<E> = effectMapper.emitEffects()

    /**
     * @return an observable which when subscribed to Dispatches results from our [resultCreator] to the dispatcher.
     */
    fun createResults(events: Observable<T>): Observable<Result> = resultCreator.dispatcheResults(events)


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
    private fun attachStateUpdates(): Disposable =  onStateUpdated.autoDispose(this).subscribe()

    fun events(lifecycleOwner: LifecycleOwner, events: () -> Observable<T>) {
        createResults(events())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(lifecycleOwner.scope()).subscribe()
    }

    fun state(lifecycleOwner: LifecycleOwner, state: (VS) -> Unit): Disposable =
        onStateUpdated
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(lifecycleOwner.scope()).subscribe { state(it) }

    fun effects(lifecycleOwner: LifecycleOwner, effects: (E) -> Unit) =
        onEffect
            .observeOn(AndroidSchedulers.mainThread())
            .autoDispose(lifecycleOwner.scope())
            .subscribe { effects(it) }

}