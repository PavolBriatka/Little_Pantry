package com.briatka.pavol.littlepantry.ui.main

import android.os.Bundle
import com.briatka.pavol.littlepantry.R
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
