package com.briatka.pavol.littlepantry.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.jakewharton.rxbinding3.widget.textChanges
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_user_contact_info.*
import javax.inject.Inject

class UserContactInfoFragment : DaggerFragment() {


    private val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var steps: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_contact_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animateViewsIn(view)
        /**
         * Even though this header is a shared element it's always recreated and steps are reset.
         * We have to "pre-set" the header to step 2 otherwise the animation from step 2 to
         * step 3 would not work.*/
        sv_registration_header.setSteps(steps)
        sv_registration_header.go(1, false)
        /**
         * Setup visibility observer so the app can run the animation when the view is
         * actually VISIBLE. It's important to remove the listener so it's only called once -
         * otherwise the app would crash.
         * */
        val treeObserver = sv_registration_header.viewTreeObserver
        treeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                treeObserver.removeOnGlobalLayoutListener(this)
                if (sv_registration_header.visibility == View.VISIBLE) {
                    sv_registration_header.go(2, true)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        updateEmailField()
        updatePhoneNumber()
        phoneNumberSubscription()
    }

    private fun phoneNumberSubscription() {
        et_phone_number.textChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (et_phone_number.tag == null) {
                    sharedViewModel.userPhoneNumber.onNext("$it")
                }
                et_phone_number.tag = null
            }.let { disposables.add(it) }
    }

    private fun updatePhoneNumber() {
        sharedViewModel.userPhoneNumber
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (et_phone_number.text.toString() != it) {
                    et_phone_number.tag = "SET"
                    et_phone_number.setText(it)
                }
            }.let { disposables.add(it) }
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

    private fun animateViewsIn(view: View) {
        val root = view.findViewById<ConstraintLayout>(R.id.cl_root)
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