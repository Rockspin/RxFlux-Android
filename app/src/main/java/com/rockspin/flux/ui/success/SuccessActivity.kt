package com.rockspin.flux.ui.success

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.rockspin.flux.R

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
       // setSupportActionBar(toolbar)


        val navController = findNavController(R.id.nav_host_fragment)
    }


    companion object{
        fun newIntent(context: Context) = Intent(context, SuccessActivity::class.java)
    }
}
