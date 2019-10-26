package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_registration_form.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class RegistrationFormFragment : DaggerFragment() {

    private lateinit var registerButton: Button
    private lateinit var newPassword: TextInputEditText

    val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_registration_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton = view.findViewById(R.id.btn_create_new_user)
        newPassword = view.findViewById(R.id.et_register_password)
    }

    override fun onStart() {
        super.onStart()
        updateEmailField()
        subscribeToFirstNameField()
        subscribeToSurnameField()
        subscribeToNickNameField()
        subscribeToEmailField()
        subscribeToRegisterButton()
        subscribeToPasswordField()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    private fun updateEmailField() {
        sharedViewModel.registerEmail
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_email_address.setText(it)
            }.let { disposables.add(it) }
    }

    private fun subscribeToFirstNameField() {
        et_user_first_name.textChanges()
            .map {
                it.trim().toString()
            }
            .subscribe(sharedViewModel.userFirstName::onNext).let { disposables.add(it) }
    }

    private fun subscribeToSurnameField() {
        et_user_surname.textChanges()
            .map {
                it.trim().toString()
            }
            .subscribe(sharedViewModel.userSurname::onNext).let { disposables.add(it) }
    }

    private fun subscribeToNickNameField() {
        et_user_nickname.textChanges()
            .map {
                it.trim().toString()
            }
            .subscribe(sharedViewModel.userNickname::onNext).let { disposables.add(it) }
    }

    private fun subscribeToEmailField() {
        et_email_address.textChanges()
            .map {
                it.trim().toString()
            }
            .subscribe(sharedViewModel.registerEmail::onNext).let { disposables.add(it) }
    }

    private fun subscribeToPasswordField() {
        et_register_password.textChanges()
            .map {
                it.trim().toString()
            }
            .subscribe(sharedViewModel.registerPassword::onNext).let { disposables.add(it) }
    }

    private fun subscribeToRegisterButton() {
        registerButton.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                sharedViewModel.startUserRegistration()
            }.let { disposables.add(it) }
    }
}
