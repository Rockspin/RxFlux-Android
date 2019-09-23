package com.rockspin.rxfluxcore

import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.*
import io.reactivex.rxkotlin.merge

/**
 * Current state of an object. for example the state of a view or the state of a hardware device,
 * or the entire state of an application.
 */
interface State

/**
 * A result modifies state.
 */
interface Result

/**
 * An [Event] represents a UiEvent that a FluxView can generate.
 * For example:
 * - pressing a button create a button pressed event.
 * - the Button Pressed Event would create a Update Label Result.
 * - the Update Label Result would modify the LabelViewState.
 *
 * These generally are fed into a [ResultCreator] which creates one or more [Result]s when then are used to update [State].
 */
interface Event

/**
 * An [Effect] is an event that should be consumed only once, like a Snackbar message, a navigation event or a dialog trigger.
 * It is useful to avoid storing items in the view model that are not cached.
 */
interface Effect

/**
 * An [Effect] that creates a new navigation action
 */
interface Navigation : Result, Effect


/**
 * A [ResultCreator] is a function that maps from [Event]s to [Result]s
 */
interface ResultCreator<T : Event> {

    fun createResults(events: Observable<T>): Observable<Result>

    fun dispatchResults(events: Observable<T>): Completable =
        events.publish(this::createResults)
            .doOnNext { Dispatcher.dispatch(it) }
            .ignoreElements()
}

/**
 * Reducers specify how the application's state changes in response to [Result]s sent to the [Dispatcher].
 * Remember that [Result]   s only describe what happened, but don't describe how the application's state changes.
 */
interface Reducer<VS : State> {

    fun reduceToState(oldState: VS, result: Result): VS
}

/**
 * An [EffectMapper] is a function that maps from [Result]s to [Effect]s
 */
interface EffectMapper<E : Effect> {
    fun mapToEffect(result: Result): E?
}

/**
 * The dispatcher receives [Result]s and dispatches them to [Store]s that have registered with the dispatcher.
 * Every store will receive every [Result]. There is only one singleton dispatcher in each application.
 */
object Dispatcher {

    private val resultsRelay = PublishRelay.create<Result>()

    val results: Observable<Result> = resultsRelay.hide()

    fun dispatch(result: Result) = resultsRelay.accept(result)
}

/**
 * A [Store] is what holds the data of an application. A stores register with the [Dispatcher] so that they can receive
 * [Result]s .The data in a store must only be mutated by responding to an [Result].
 *
 * A [Store] only caches view state when someone is subscribed to the observable returned from [updates]
 *
 */
abstract class Store<VS : State>(
    val reducer: Reducer<VS>,
    val initialState: Single<VS>
) {


    /**
     * emits updates to the store, should cache the result between subscriptions.
     * (see [com.jakewharton.rx.ReplayingShare])
     */
    val updates: Observable<VS> = initialState
        .flatMapObservable { state ->
            Dispatcher.results.scan(state) { oldState, result ->
                reducer.reduceToState(
                    oldState,
                    result
                )
            }
        }
        .distinctUntilChanged()
        .replayingShare()
}

/**
 * An effect store emits.
 */
class EffectStore<E : Effect>(effectMapper: EffectMapper<E>) {

    val updates: Observable<E> =
        Dispatcher
            .results
            .flatMap {
                val effect = effectMapper.mapToEffect(it)
                if (effect == null) {
                    Observable.empty()
                } else {
                    Observable.just(effect)
                }
            }

}

/**
 * Data from stores is displayed in views. Then when the store emits a change the view can get the new data and re-render.
 */
interface FluxView<T : Event, U : State, E : Effect> {
    fun events(): Observable<T>

    /**
     * View should redraw its self to represent the new [viewState].
     * @param viewState representation of the views current state.
     */
    fun stateChanged(viewState: U)

    /**
     * View should handle [effect].
     * @param effect [Effect] to handle
     */
    fun receivedEffect(effect: E)

    /**
     * View should handle [navigation].
     * @param navigation [Navigation] to handle
     */
    fun receivedNavigation(navigation: Navigation)
}


/**
 * A basic Store that can be used in most cases.
 */
class BasicStore<VS : State>(
    reducer: Reducer<VS>,
    initialState: Single<VS>
) : Store<VS>(reducer, initialState)


fun <VS : State> Reducer<VS>.toStore(initialState: Single<VS>): Store<VS> =
    BasicStore(this, initialState)

fun <VS : State> Reducer<VS>.toStore(initialState: VS): Store<VS> =
    BasicStore(this, Single.just(initialState))

fun <E : Effect> EffectMapper<E>.toStore(): EffectStore<E> =
    EffectStore(this)

fun <VS : State> combineReducers(vararg reducers: Reducer<VS>): Reducer<VS> =
    object : Reducer<VS> {
        override fun reduceToState(oldState: VS, result: Result): VS {
            return reducers.fold(oldState) { acc, reducer -> reducer.reduceToState(acc, result) }
        }
    }


fun <T : Event> combineActionCreators(vararg resultCreators: ResultCreator<T>): ResultCreator<T> {
    return object : ResultCreator<T> {
        override fun createResults(events: Observable<T>): Observable<Result> =
            resultCreators.map { it.createResults(events) }.toList().merge()
    }
}
