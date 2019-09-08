package com.rockspin.rxfluxandroid

import androidx.fragment.app.Fragment
import com.rockspin.rxfluxcore.Effect
import com.rockspin.rxfluxcore.Event
import com.rockspin.rxfluxcore.FluxView
import com.rockspin.rxfluxcore.State

abstract class FluxFragment<T : Event, U : State, E : Effect> : Fragment(),
    FluxView<T, U, E>