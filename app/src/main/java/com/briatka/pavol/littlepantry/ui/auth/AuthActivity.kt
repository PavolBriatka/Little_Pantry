package com.briatka.pavol.littlepantry.ui.auth

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var viewModelProviderFactory: ViewModelsProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
    }
}
