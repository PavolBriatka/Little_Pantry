package com.briatka.pavol.littlepantry.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
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

    override fun onStart() {
        super.onStart()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.isExistingUser.observe(this, Observer<Boolean> { isExistingUser ->
            Toast.makeText(this, "Is existing user: $isExistingUser", Toast.LENGTH_LONG).show()
        })
    }
}
