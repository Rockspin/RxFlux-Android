package com.rockspin.flux.ui.success.fragment1

import com.rockspin.rxfluxandroid.FluxViewModel
import com.rockspin.rxfluxcore.EffectMapper
import com.rockspin.rxfluxcore.Reducer
import com.rockspin.rxfluxcore.Result
import com.rockspin.rxfluxcore.ResultCreator
import io.reactivex.Observable

class Fragment1ViewModel(
    reducer: Fragment1Reducer,
    resultCreator: Fragment1ResultCreator,
    effectMapper: Fragment1EffectMapper
) : FluxViewModel<Fragment1Events, Fragment1State, Fragment1Effects>(
        reducer = reducer,
        initialState = Fragment1State(),
        resultCreator = resultCreator,
        effectMapper = effectMapper
)


class Fragment1Reducer : Reducer<Fragment1State> {
    override fun reduceToState(oldState: Fragment1State, result: Result): Fragment1State =
            oldState

}

class Fragment1ResultCreator : ResultCreator<Fragment1Events> {
    override fun createResults(events: Observable<Fragment1Events>): Observable<Result> =
            events.map {
                when (it) {
                    is Fragment1Events.clickedOpen -> it
                    else -> object: Result{}
                }
            }

}

class Fragment1EffectMapper : EffectMapper<Fragment1Effects> {
    override fun mapToEffect(result: Result): Fragment1Effects? {
        return when (result) {
            is Fragment1Events.clickedOpen -> Fragment1Effects.openFragment2
            else -> null
        }
    }

}