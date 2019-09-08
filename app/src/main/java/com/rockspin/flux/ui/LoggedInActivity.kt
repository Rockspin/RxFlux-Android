package com.rockspin.flux.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rockspin.flux.R


class LoggedInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)
    }

    companion object{
         fun newIntent(context: Context) = Intent(context, LoggedInActivity::class.java)
    }
}
