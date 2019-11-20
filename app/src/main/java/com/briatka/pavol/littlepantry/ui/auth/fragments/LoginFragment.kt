package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState.*
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_EMAIL_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_PASSWORD_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_USER_NOT_EXIST
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_USER_ALREADY_EXIST
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : DaggerFragment() {

    private val sharedViewModel: AuthViewModel by activityViewModels()
    private var disposables = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        subscribeObserver()
        subscribeToLoginButton()
        subscribeToRegisterButton()
        subscribeToLoginEmailField()
        subscribeToLoginPasswordField()
    }

    private fun subscribeObserver() {
        sharedViewModel.authState.removeObservers(viewLifecycleOwner)
        sharedViewModel.authState.observe(viewLifecycleOwner, Observer { userState ->
            when (userState) {
                is AuthInProgress -> {
                    changeLoadingBarVisibility(userState.flag)
                }
                AuthCreateNewUser -> {
                    changeLoadingBarVisibility()
                }
                is AuthEmailVerificationFailure -> {
                    changeLoadingBarVisibility()
                    onCredentialsError(userState.error)
                }
                AuthUserNotExist -> {
                    changeLoadingBarVisibility()
                    onCredentialsError(LOGIN_USER_NOT_EXIST)
                }
                AuthUserAlreadyExist -> {
                    changeLoadingBarVisibility()
                    onCredentialsError(REGISTER_USER_ALREADY_EXIST)
                }
                is AuthLoginFailure -> {
                    changeLoadingBarVisibility()
                    Snackbar.make(
                        requireView(),
                        getString(R.string.login_error_unknown),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                AuthWrongPassword -> {
                    onCredentialsError(LOGIN_PASSWORD_ERROR)
                    changeLoadingBarVisibility()
                }
            }
        })
    }

    private fun subscribeToLoginButton() {
        btn_login_email_password.clicks()
            .subscribe {
                when {
                    et_email_address.text.isNullOrBlank() -> tl_email_address.error =
                        requireContext().getString(R.string.login_error_missing_email)
                    et_login_password.text.isNullOrBlank() -> tl_login_password.error =
                        requireContext().getString(R.string.login_error_missing_password)
                    else -> sharedViewModel.startUserVerification(LOGIN_FLAG)
                }
            }.let { disposables.add(it) }
    }

    private fun subscribeToRegisterButton() {
        tv_sign_up.clicks()
            .subscribe {
                when {
                    et_email_address.text.isNullOrBlank() -> tl_email_address.error =
                        requireContext().getString(R.string.login_error_missing_email)
                    et_login_password.text.isNullOrBlank() -> tl_login_password.error =
                        requireContext().getString(R.string.login_error_missing_password)
                    else -> sharedViewModel.startUserVerification(REGISTER_FLAG)
                }
            }.let { disposables.add(it) }
    }

    private fun subscribeToLoginEmailField() {
        et_email_address.textChanges()
            .map {
                it.toString().trim()
            }
            .subscribe {
                sharedViewModel.userEmail.onNext(it)
                tl_email_address.error = null
            }.let { disposables.add(it) }
    }

    private fun subscribeToLoginPasswordField() {
        et_login_password.textChanges()
            .map {
                it.toString().trim()
            }
            .subscribe {
                sharedViewModel.userPassword.onNext(it)
                tl_login_password.error = null
            }.let { disposables.add(it) }
    }

    private fun changeLoadingBarVisibility(flag: String? = null) {

        when (flag) {
            null -> {
                skv_loading_bar_login.visibility = View.GONE
                skv_loading_bar_register.visibility = View.GONE
                btn_login_email_password.text = requireContext().getString(R.string.log_in_label)
                tv_sign_up.text = requireContext().getString(R.string.sign_up_label)
            }
            LOGIN_FLAG -> {
                skv_loading_bar_login.visibility = View.VISIBLE
                btn_login_email_password.text = ""
            }
            REGISTER_FLAG -> {
                skv_loading_bar_register.visibility = View.VISIBLE
                tv_sign_up.text = ""
            }
        }
    }

    private fun onCredentialsError(error: String) {
        when (error) {
            LOGIN_EMAIL_ERROR -> {
                tl_email_address.error = requireContext().getString(R.string.login_malformed_email)
            }
            LOGIN_PASSWORD_ERROR -> {
                tl_login_password.error = requireContext().getString(R.string.login_wrong_password)
            }
            LOGIN_USER_NOT_EXIST -> {
                tl_email_address.error =
                    requireContext().getString(R.string.login_error_user_not_exist)
            }
            REGISTER_USER_ALREADY_EXIST -> {
                tl_email_address.error =
                    requireContext().getString(R.string.login_error_user_already_exist)
            }
        }
    }
}
