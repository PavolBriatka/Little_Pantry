package com.briatka.pavol.littlepantry.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState.*
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var navController: NavController

    @Inject
    lateinit var viewModelProviderFactory: ViewModelsProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
        navController = this.findNavController(R.id.auth_nav_host_fragment)

    }

    override fun onStart() {
        super.onStart()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.userState.observe(this, Observer<AuthUserState> { userState ->
            when (userState) {
                AuthInProgress -> pb_login.visibility = View.VISIBLE
                AuthCreateNewUser -> navController.navigate(R.id.action_start_registration)
                AuthRegistrationSuccessful -> navController.navigate(R.id.action_collect_user_info)
                 is AuthRegistrationFailure -> Toast.makeText(this, userState.error, Toast.LENGTH_LONG).show()
                AuthRegistrationFinalized -> navController.navigate(R.id.action_open_main_activity)
            }
        })
    }
}
