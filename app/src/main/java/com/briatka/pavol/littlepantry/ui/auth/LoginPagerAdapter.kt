package com.briatka.pavol.littlepantry.ui.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_EMAIL_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_PASSWORD_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_USER_NOT_EXIST
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_EMAIL_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_USER_ALREADY_EXIST
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.disposables.CompositeDisposable

class LoginPagerAdapter(private val context: Context, private val sharedViewModel: AuthViewModel) :
    PagerAdapter() {


    private var disposables = CompositeDisposable()

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var loginEmail: TextInputEditText
    private lateinit var loginPassword: TextInputEditText
    private lateinit var registerEmail: TextInputEditText

    private lateinit var loginEmailWrapper: TextInputLayout
    private lateinit var loginPasswordWrapper: TextInputLayout
    private lateinit var registerEmailWrapper: TextInputLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pagerEnum = PagerEnum.values()[position]
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(pagerEnum.layoutId, container, false)

        when (layout.id) {
            R.id.cl_login -> {
                loginButton = layout.findViewById(R.id.btn_login_email_password)
                loginEmail = layout.findViewById(R.id.et_email_address)
                loginPassword = layout.findViewById(R.id.et_login_password)

                loginEmailWrapper = layout.findViewById(R.id.tl_email_address)
                loginPasswordWrapper = layout.findViewById(R.id.tl_login_password)

                subscribeToLoginButton()
                subscribeToLoginEmailField()
                subscribeToLoginPasswordField()
            }
            R.id.cl_register -> {
                registerButton = layout.findViewById(R.id.btn_register_email_password)
                registerEmail = layout.findViewById(R.id.et_email_address)

                registerEmailWrapper = layout.findViewById(R.id.tl_email_address)

                subscribeToRegisterButton()
                subscribeToRegisterEmailField()
            }
        }
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val pagerEnum = PagerEnum.values()[position]
        return context.getString(pagerEnum.title)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return PagerEnum.values().size
    }

    private fun subscribeToLoginButton() {
        loginButton.clicks()
            .subscribe {
                when {
                    loginEmail.text?.isBlank()!! -> loginEmailWrapper.error =
                        context.getString(R.string.login_error_missing_email)
                    loginPassword.text?.isBlank()!! -> loginPasswordWrapper.error =
                        context.getString(R.string.login_error_missing_password)
                    else -> sharedViewModel.startUserVerification(LOGIN_FLAG)
                }
            }.let { disposables.add(it) }
    }

    private fun subscribeToRegisterButton() {
        registerButton.clicks()
            .subscribe {
                if (registerEmail.text?.isBlank()!!) {
                    registerEmailWrapper.error =
                        context.getString(R.string.login_error_missing_email)
                } else {
                    sharedViewModel.startUserVerification(REGISTER_FLAG)
                }
            }.let { disposables.add(it) }
    }

    private fun subscribeToLoginEmailField() {
        loginEmail.textChanges()
            .map {
                it.toString().trim()
            }
            .subscribe {
                sharedViewModel.loginEmail.onNext(it)
                loginEmailWrapper.isErrorEnabled = false
            }.let { disposables.add(it) }
    }

    private fun subscribeToLoginPasswordField() {
        loginPassword.textChanges()
            .map {
                it.toString().trim()
            }
            .subscribe {
                sharedViewModel.loginPassword.onNext(it)
                loginPasswordWrapper.isErrorEnabled = false
            }.let { disposables.add(it) }
    }

    private fun subscribeToRegisterEmailField() {
        registerEmail.textChanges()
            .map {
                it.toString().trim()
            }
            .subscribe {
                sharedViewModel.registerEmail.onNext(it)
                registerEmailWrapper.isErrorEnabled = false
            }.let { disposables.add(it) }
    }

    fun onCredentialsError(error: String) {
        when (error) {
            LOGIN_EMAIL_ERROR -> {
                loginEmail.error = context.getString(R.string.login_malformed_email)
            }
            LOGIN_PASSWORD_ERROR -> {
                loginPasswordWrapper.error = context.getString(R.string.login_wrong_password)
            }
            LOGIN_USER_NOT_EXIST -> {
                loginEmailWrapper.error = context.getString(R.string.login_error_user_not_exist)
            }
            REGISTER_EMAIL_ERROR -> {
                registerEmail.error = context.getString(R.string.login_malformed_email)
            }
            REGISTER_USER_ALREADY_EXIST -> {
                registerEmailWrapper.error =
                    context.getString(R.string.login_error_user_already_exist)
            }
        }
        notifyDataSetChanged()
    }

    fun dispose() {
        disposables.dispose()
    }
}