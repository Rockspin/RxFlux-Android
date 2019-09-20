package com.rockspin.flux.ui.success.fragment1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding3.view.clicks
import com.rockspin.flux.R
import com.rockspin.rxfluxandroid.FluxFragment
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.fragment_sensors.*
import org.koin.android.viewmodel.ext.android.viewModel

class Fragment1 : FluxFragment<Fragment1Events, Fragment1State, Fragment1Effects>() {

    override val fluxViewModel: Fragment1ViewModel by viewModel()

    override fun events(): Observable<Fragment1Events> {
       return  listOf(
            leftTrackerButton.clicks().map { Fragment1Events.clickedOpen }
        ).merge<Fragment1Events>().startWith(Fragment1Events.empty)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sensors, container, false).apply {

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun stateChanged(viewState: Fragment1State) {
    }

    override fun receivedEffect(effect: Fragment1Effects) {
        when (effect) {
            is Fragment1Effects.openFragment2 -> findNavController().navigate(R.id.action_fragment1_to_fragment2)
        }
    }
}