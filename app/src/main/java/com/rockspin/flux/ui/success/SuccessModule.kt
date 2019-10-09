package com.rockspin.flux.ui.success

import com.rockspin.flux.ui.success.fragment1.Fragment1EffectMapper
import com.rockspin.flux.ui.success.fragment1.Fragment1ViewModel
import com.rockspin.flux.ui.success.fragment2.Fragment2ViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val successModule = module {
    // select sensor
    factory { Fragment1EffectMapper() }
    viewModel { Fragment1ViewModel(get()) }

    // setup sensor
    viewModel { Fragment2ViewModel() }
}