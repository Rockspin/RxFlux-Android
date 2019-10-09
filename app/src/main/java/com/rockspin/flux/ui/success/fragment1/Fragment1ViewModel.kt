package com.rockspin.flux.ui.success.fragment1

import com.rockspin.rxfluxandroid.FluxViewModel
import com.rockspin.rxfluxcore.*
import io.reactivex.Observable

class Fragment1ViewModel(
    effectMapper: Fragment1EffectMapper
) : FluxViewModel<Fragment1Events, Fragment1State, Fragment1Effects>(
        initialState = Fragment1State(),
        effectMapper = effectMapper
)

class Fragment1EffectMapper : SimpleEffectMapper<Fragment1Effects> {
    override fun mapToEffect(result: Result): Fragment1Effects? {
        return when (result) {
            is Fragment1Events.clickedOpen -> Fragment1Effects.openFragment2
            else -> null
        }
    }

}