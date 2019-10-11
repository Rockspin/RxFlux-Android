import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rx.replayingShare
import com.rockspin.rxfluxandroid.FluxModel
import com.rockspin.rxfluxcore.*
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDispose
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class ApplicationFluxModel<T : Event, VS : State, E : Effect>(
    private val store: Store<VS>,
    effectMapper: EffectMapper<E> = emptyEffectMapper(),
    resultCreator: ResultCreator<T> = emptyResultCreator()
) : FluxModel<T, VS, E> {

    constructor(
        initialState: VS,
        reducer: Reducer<VS> = emptyReducer(),
        effectMapper: EffectMapper<E> = emptyEffectMapper(),
        resultCreator: ResultCreator<T> = emptyResultCreator()
    ) : this(reducer.toStore(Single.just(initialState)), effectMapper, resultCreator)

    private val eventRelay : EventRelay<T> = EventRelay()
    private var disposeable: CompositeDisposable? = null

    val results = eventRelay.events.publish(resultCreator::dispatcheResults).replayingShare()

    fun dispatchEvent(event: T) {
        eventRelay.dispatch(event)
    }

    fun connect() {
        check(!(disposeable != null && !disposeable?.isDisposed!!)) { "Already connected" }

        disposeable = CompositeDisposable(
            store.updates.subscribe(),
            results.subscribe()
        )
    }

    fun disconnect() {
        disposeable?.dispose()
    }


    override fun currentState(): VS = store.currentViewState

    override val onStateUpdated: Observable<VS> = store.updates

    override val onEffect : Observable<E> = effectMapper.emitEffects()


    fun events(lifecycleOwner: LifecycleOwner, events: () -> Observable<T>) {
        events()
            .autoDispose(lifecycleOwner.scope())
            .subscribe {   eventRelay.dispatch(it) }
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
