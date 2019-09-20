package com.rockspin.flux.ui.success.fragment2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rockspin.flux.R
import org.koin.android.viewmodel.ext.android.viewModel

class Fragment2 : Fragment() {

    val sensorViewModel: Fragment2ViewModel  by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_setup_sensor, container, false).apply {


            }


}
