package com.rockspin.rxfluxandroid

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.rockspin.rxfluxcore.*
import com.rockspin.rxfluxcore.cached.ViewStateCache
import com.rockspin.rxfluxcore.cached.toCachedStore
import com.rockspin.rxfluxcore.multistate.MultiState
import com.rockspin.rxfluxcore.multistate.MultiStateReducer
import com.uber.autodispose.android.lifecycle.autoDisposable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.lang.IllegalStateException

abstract class FluxViewModel<T : Event, VS : State, E : Effect>(
    val store: Store<VS>,
    val effectMapper: EffectStore<E>? = null,
    val resultCreator: ResultCreator<T>? = null
) : ViewModel(){

    constructor(
        reducer: Reducer<VS>,
        initialState: VS,
        effectMapper: EffectStore<E>? = null,
        resultCreator: ResultCreator<T>? = null
    ) : this(reducer.toStore(Single.just(initialState)), effectMapper, resultCreator)

    constructor(
        reducer: Reducer<VS>,
        initialState: Single<VS>,
        effectMapper: EffectStore<E>? = null,
        resultCreator: ResultCreator<T>? = null
    ) : this(reducer.toStore(initialState), effectMapper, resultCreator)

    constructor(
        reducer: Reducer<VS>,
        viewStateCache: ViewStateCache<VS>,
        effectMapper: EffectStore<E>? = null,
        resultCreator: ResultCreator<T>? = null
    ) : this(reducer.toCachedStore(viewStateCache), effectMapper, resultCreator)

}

abstract class FluxActivity<T : Event, U : State, E : Effect> : AppCompatActivity(),
    FluxView<T, U, E> {
    abstract val viewModel: FluxViewModel<T,U,E>
    private val observerOn: Scheduler = AndroidSchedulers.mainThread()

    override fun receivedEffect(effect: E) { }

    override fun receivedNavigation(effect: Navigation) { }

    fun dispatchResults(): Disposable {
        val resultCreator = viewModel.resultCreator ?: throw IllegalStateException("no effect mapper on view model")
        return resultCreator.dispatchResults(events).observeOn(observerOn).autoDisposable(this).subscribe()
    }

    fun attachStateUpdates(): Disposable =
        viewModel.store.updates.observeOn(observerOn).autoDisposable(this).subscribe(this::stateChanged)

    fun attachEffectUpdates(): Disposable {
        val effectMapper = viewModel.effectMapper ?: throw IllegalStateException("no effect mapper on view model")
        return effectMapper.updates.observeOn(observerOn).autoDisposable(this).subscribe(this::receivedEffect)
    }
}


abstract class FluxFragment<T : Event, U : State, E : Effect> : Fragment(), FluxView<T, U, E>

fun <E : Event> ResultCreator<E>.dispatchResults(
    events: Observable<E>,
    lifecycleOwner: LifecycleOwner
): Disposable = this.dispatchResults(events).autoDisposable(lifecycleOwner).subscribe()