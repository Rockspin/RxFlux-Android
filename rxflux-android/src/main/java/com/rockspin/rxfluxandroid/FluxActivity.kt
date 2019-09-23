package com.rockspin.rxfluxandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.rockspin.rxfluxcore.*
import com.uber.autodispose.android.lifecycle.autoDisposable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.lang.IllegalStateException

/**
 * Activity that
 */
abstract class FluxActivity<T : Event, U : State, E : Effect> : AppCompatActivity(),
    FluxView<T, U, E> {
    abstract val fluxViewModel: FluxViewModel<T, U, E>
    private val observerOn: Scheduler =
        AndroidSchedulers.mainThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // start listening for state changes when the activity is created.
        listenForStateUpdates()
    }

    override fun onResume() {
        super.onResume()
        listenForEffectUpdates(Lifecycle.Event.ON_PAUSE)
        createAndDispatchResults(Lifecycle.Event.ON_PAUSE)

    }

    override fun receivedEffect(effect: E) {}

    override fun receivedNavigation(navigation: Navigation) {}

    /**
     *  Register the events this activity generates to a ResultCreator
     *  then send the results to the dispatcher.
     *
     * The subscription is automatically disposed when the activity is destroyed or when
     * [untilEvent] occurs.
     *
     *  @param untilEvent Optional lifecycle event when subscription will be disposed.
     */
    fun createAndDispatchResults(untilEvent: Lifecycle.Event? = null): Disposable {
        val resultCreator = fluxViewModel.resultCreator
            ?: throw IllegalStateException("no effect mapper on view model")
        return resultCreator.dispatchResults(events()).observeOn(observerOn)
            .autoDisposable(this, untilEvent).subscribe()
    }

    /**
     * Register this activities to listens for state updates.
     *
     * The subscription is automatically disposed when the activity is destroyed or when
     * [untilEvent] occurs.
     *
     * @param untilEvent Optional lifecycle event when subscription will be disposed.
     */
    fun listenForStateUpdates(untilEvent: Lifecycle.Event? = null): Disposable =
        fluxViewModel.store.updates.observeOn(observerOn).autoDisposable(
            this,
            untilEvent
        ).subscribe(this::stateChanged)

    /**
     * Register this activities to listens for effect updates.
     *
     * The subscription is automatically disposed when the activity is destroyed or when
     * [untilEvent] occurs.
     *
     * @param untilEvent Optional lifecycle event when subscription will be disposed.
     */
    fun listenForEffectUpdates(untilEvent: Lifecycle.Event? = null): Disposable {
        val effectMapper = fluxViewModel.effectStore
            ?: throw IllegalStateException("no effect mapper on view model")
        return effectMapper.updates.observeOn(observerOn).autoDisposable(this, untilEvent)
            .subscribe(this::receivedEffect)
    }
}