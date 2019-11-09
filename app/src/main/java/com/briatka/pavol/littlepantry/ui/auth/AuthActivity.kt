package com.briatka.pavol.littlepantry.ui.auth

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.briatka.pavol.littlepantry.ConnectivityLiveData
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState.*
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var navController: NavController
    private lateinit var connectivitySnackbar: Snackbar

    @Inject
    lateinit var viewModelProviderFactory: ViewModelsProviderFactory
    @Inject
    lateinit var connectivityStatus: ConnectivityLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.briatka.pavol.littlepantry.R.layout.activity_auth)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
        navController =
            this.findNavController(com.briatka.pavol.littlepantry.R.id.auth_nav_host_fragment)

        connectivitySnackbar = Snackbar.make(
            fl_auth_activity,
            getString(com.briatka.pavol.littlepantry.R.string.snackbar_lost_connection_message),
            Snackbar.LENGTH_INDEFINITE
        )
    }

    override fun onStart() {
        super.onStart()
        subscribeAuthenticationStatus()
        subscribeToConnectivityStatus()
    }

    override fun onResume() {
        super.onResume()
        enableFullscreen()
    }

    private fun enableFullscreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun subscribeToConnectivityStatus() {
        connectivityStatus.observe(this, Observer<Boolean> { isConnected ->
            if (isConnected) {
                connectivitySnackbar.dismiss()
            } else {
                connectivitySnackbar.show()
            }
        })
    }

    private fun subscribeAuthenticationStatus() {
        viewModel.authState.observe(this, Observer<AuthUserState> { userState ->
            when (userState) {
                AuthInProgress -> {
                }
                AuthCreateNewUser -> {
                    navController.navigate(com.briatka.pavol.littlepantry.R.id.action_start_registration)

                }
                AuthRegistrationSuccessful -> navController.navigate(com.briatka.pavol.littlepantry.R.id.action_collect_user_info)
                AuthRegistrationFinalized -> navController.navigate(com.briatka.pavol.littlepantry.R.id.action_open_main_activity)
                AuthLoginSuccessful -> navController.navigate(com.briatka.pavol.littlepantry.R.id.action_open_main_activity)
                is AuthRegistrationFailure -> Toast.makeText(
                    this,
                    userState.error,
                    Toast.LENGTH_LONG
                ).show()
                is AuthEmailVerificationFailure,
                AuthUserNotExist,
                AuthUserAlreadyExist,
                is AuthLoginFailure,
                AuthWrongPassword -> {
                }

            }
        })
    }
}
