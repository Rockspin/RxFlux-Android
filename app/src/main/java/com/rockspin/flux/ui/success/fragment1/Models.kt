package com.rockspin.flux.ui.success.fragment1

import com.rockspin.rxfluxcore.*


sealed class Fragment1Events : Event {
    object clickedOpen : Fragment1Events(), Result
    object empty : Fragment1Events(), Result
}

data class Fragment1State(val loading: Boolean = false) : State


sealed class Fragment1Effects : Effect {
    object openFragment2 : Fragment1Effects()
}

