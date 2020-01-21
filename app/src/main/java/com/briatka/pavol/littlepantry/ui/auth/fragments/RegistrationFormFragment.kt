package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_registration_form.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RegistrationFormFragment : DaggerFragment() {


    private val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()
    @Inject
    lateinit var steps: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_registration_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sv_registration_header.setSteps(steps)
        animateViewsIn(view)
    }

    override fun onStart() {
        super.onStart()
        updateEmailField()
        updatePasswordField()
        subscribeToFirstNameField()
        subscribeToSurnameField()
        subscribeToNickNameField()
        subscribeToRegisterButton()
        subscribeToPasswordField()
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    private fun updateEmailField() {
        sharedViewModel.userEmail
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_email_address.setText(it)
                et_email_address.isEnabled = false
            }.let { disposables.add(it) }
    }

    private fun updatePasswordField() {
        sharedViewModel.userPassword
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_register_password.setText(it)
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

    private fun subscribeToPasswordField() {
        et_register_password.textChanges()
            .map {
                it.trim().toString()
            }
            .subscribe(sharedViewModel.userPassword::onNext).let { disposables.add(it) }
    }

    private fun subscribeToRegisterButton() {
        btn_create_new_user.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                sharedViewModel.startUserRegistration()
            }.let { disposables.add(it) }
    }

    private fun animateViewsIn(view: View) {
        val root = view.findViewById<CoordinatorLayout>(R.id.cl_root)
        val count = root.childCount
        var offset = resources.getDimensionPixelSize(R.dimen.offset_y).toFloat()
        val interpolator =
            AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in)

        for (i in 1 until count) {
            val mView = root.getChildAt(i)
            mView.translationX = offset
            mView.alpha = 0.5f
            mView.animate()
                .translationX(0f)
                .alpha(1f)
                .setInterpolator(interpolator)
                .setDuration(600L)
                .start()
            offset *= 1.5f
        }
    }
}
