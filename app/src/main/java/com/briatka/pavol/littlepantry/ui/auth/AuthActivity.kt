package com.briatka.pavol.littlepantry.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.briatka.pavol.littlepantry.ConnectivityLiveData
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.UserState
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.UserState.*
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_user_contact_info.sv_registration_header
import javax.inject.Inject

class AuthActivity : DaggerAppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var mainNavController: NavController
    private lateinit var connectivitySnackbar: Snackbar

    @Inject
    lateinit var viewModelProviderFactory: ViewModelsProviderFactory
    @Inject
    lateinit var connectivityStatus: ConnectivityLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(AuthViewModel::class.java)
        mainNavController =
            this.findNavController(R.id.auth_nav_host_fragment)

        connectivitySnackbar = Snackbar.make(
            fl_auth_activity,
            getString(R.string.snackbar_lost_connection_message),
            Snackbar.LENGTH_INDEFINITE
        )

        subscribeAuthenticationStatus()
        subscribeToConnectivityStatus()
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
        viewModel.userState.observe(this, Observer<UserState> { userState ->
            when (userState) {
                CreateNewUser -> {
                    mainNavController.navigate(R.id.action_start_registration)
                }
                RegistrationSuccessful -> {
                    val extras = FragmentNavigatorExtras(
                        sv_registration_header to "header_step_view"
                    )
                    mainNavController.navigate(R.id.action_collect_user_info, null, null, extras)
                }
                ProfilePictureUploadSuccessful -> {
                    val extras = FragmentNavigatorExtras(
                        sv_registration_header to "header_step_view"
                    )
                    mainNavController.navigate(
                        R.id.action_register_user_contact_info,
                        null,
                        null,
                        extras
                    )
                }
                RegistrationFinalized -> mainNavController.navigate(R.id.action_open_main_activity)
                AuthLoginSuccessful -> mainNavController.navigate(R.id.action_open_main_activity)
                is RegistrationFailure -> Toast.makeText(
                    this,
                    userState.error,
                    Toast.LENGTH_LONG
                ).show()
                is AuthEmailVerificationFailure,
                AuthUserNotExist,
                UserAlreadyExist,
                is AuthLoginFailure,
                AuthWrongPassword -> {
                }

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
