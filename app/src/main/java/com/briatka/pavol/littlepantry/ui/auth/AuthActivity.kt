package com.briatka.pavol.littlepantry.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import com.jakewharton.rxbinding3.view.clicks
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private val disposables = CompositeDisposable()

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
        subscribeToClicks()
    }

    private fun subscribeToClicks() {
        sign_in_button.clicks()
            .subscribe {
                val email = tv_email.text.toString().trim()
                viewModel.startUserVerification(email)
            }?.let { disposable -> disposables.add(disposable) }
    }

    private fun subscribeObserver() {
        viewModel.isExistingUser.observe(this, Observer<Boolean> { isExistingUser ->
            Toast.makeText(this,"Is existing user: $isExistingUser", Toast.LENGTH_LONG).show()
        })
    }
}
