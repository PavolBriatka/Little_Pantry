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
import io.reactivex.disposables.CompositeDisposable
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
        subscribeToRegisterButton()
        subscribeToPasswordField()
    }

    private fun subscribeToPasswordField() {
        newPassword.textChanges()
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
