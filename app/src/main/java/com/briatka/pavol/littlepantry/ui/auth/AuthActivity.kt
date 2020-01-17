package com.briatka.pavol.littlepantry.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REQUEST_PHONE_STATE
import com.briatka.pavol.littlepantry.utils.CountryHelper
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_user_contact_info.*
import java.util.*
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
                    /**
                     * After a successful upload we are going to collect contact information from the user.
                     * To automate the process we are going to try and get the phone number directly from
                     * the device (allowed from API >= 26) or at least country code of SIM card. To access
                     * either of the information we need user's permission to READ PHONE STATE*/
                    checkPermissions()

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

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), REQUEST_PHONE_STATE)
        } else {
            if (Build.VERSION.SDK_INT >= 26) setPhoneNumber() else setPhoneCode()
        }
    }

    private fun setPhoneCode() {
        try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = telephonyManager.simCountryIso

            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                 viewModel.userPhoneNumber.onNext(CountryHelper.getCurrencySymbolFromCode(simCountry.toLowerCase(Locale.US)))
            } else if (telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)

                val networkCountry = telephonyManager.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                     viewModel.userPhoneNumber.onNext(CountryHelper.getCurrencySymbolFromCode(networkCountry.toLowerCase(Locale.US)))
                }
            }
        } catch (e: Exception) {
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    @RequiresApi(26)
    private fun setPhoneNumber() {
        try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val phoneNumber = telephonyManager.line1Number

            if (phoneNumber != null) {
                viewModel.userPhoneNumber.onNext("+$phoneNumber")
            } else {
                //If we cannot get the whole phone number we can try to set at least the country phone code
                setPhoneCode()
            }
        } catch (ex: Exception) {
            Log.e("", ex.message)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PHONE_STATE) {
            if (Build.VERSION.SDK_INT >= 26) setPhoneNumber() else setPhoneCode()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
