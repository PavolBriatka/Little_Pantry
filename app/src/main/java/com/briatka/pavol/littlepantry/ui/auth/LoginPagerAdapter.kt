package com.briatka.pavol.littlepantry.ui.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class LoginPagerAdapter(private val context: Context, private val sharedViewModel: AuthViewModel) :
    PagerAdapter() {

    private var disposables = CompositeDisposable()

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var loginEmail: TextInputEditText
    private lateinit var loginPassword: TextInputEditText

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pagerEnum = PagerEnum.values()[position]
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(pagerEnum.layoutId, container, false)

        when (layout.id) {
            R.id.cl_login -> {
                loginButton = layout.findViewById(R.id.btn_login_email_password)
                loginEmail = layout.findViewById(R.id.et_email_address)
                loginPassword = layout.findViewById(R.id.et_password)
                subscribeToLoginButton()
                subscribeToEmailField()
                subscribeToPasswordField()
            }
            R.id.cl_register -> {
                registerButton = layout.findViewById(R.id.btn_register_email_password)
                subscribeToRegisterButton()
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
                sharedViewModel.startUserVerification()
            }.let { disposables.add(it) }
    }

    private fun subscribeToRegisterButton() {
        registerButton.clicks()
            .subscribe {

            }.let { disposables.add(it) }
    }

    private fun subscribeToEmailField() {
        loginEmail.textChanges()
            .debounce(500, TimeUnit.MILLISECONDS)
            .map {
                it.toString().trim()
            }
            .subscribe(sharedViewModel.loginEmail::onNext).let { disposables.add(it) }
    }

    private fun subscribeToPasswordField() {
        loginPassword.textChanges()
            .debounce(500, TimeUnit.MILLISECONDS)
            .map {
                it.toString().trim()
            }
            .subscribe(sharedViewModel.loginPassword::onNext).let { disposables.add(it) }
    }

    fun dispose() {
        disposables.dispose()
    }
}