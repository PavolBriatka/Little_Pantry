package com.briatka.pavol.littlepantry.ui.auth.fragments

import android.animation.ValueAnimator
import android.content.Context
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
import com.briatka.pavol.littlepantry.utils.AuthConstants
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.shuhart.stepview.StepView
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_user_contact_info.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserContactInfoFragment : DaggerFragment() {


    private val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()

    private lateinit var headerView: StepView
    private lateinit var mContext: Context

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

        mContext = requireContext()
        return inflater.inflate(R.layout.fragment_user_contact_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView = view.findViewById(R.id.sv_registration_header)

        animateViewsIn(view)
        /**
         * Even though this header is a shared element it's always recreated and steps are reset.
         * We have to "pre-set" the header to step 2 otherwise the animation from step 2 to
         * step 3 would not work.*/
        headerView.setSteps(steps)
        headerView.go(1, false)
        /**
         * Setup visibility observer so the app can run the animation when the view is
         * actually VISIBLE. It's important to remove the listener so it's only called once -
         * otherwise the app would crash.
         * */
        val treeObserver = headerView.viewTreeObserver
        treeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                treeObserver.removeOnGlobalLayoutListener(this)
                if (headerView.visibility == View.VISIBLE) {
                    headerView.go(2, true)
                }
            }
        })

        KeyboardVisibilityEvent.setEventListener(
            requireActivity()
        ) { isVisible ->
            if (isVisible) {
                animateHeader(true)
            } else {
                animateHeader(false)
            }
        }
    }

    private fun animateHeader(scrollUp: Boolean) {
        if (scrollUp) {
            val headerAnim = provideHeaderScrollAnimation(AuthConstants.HEADER_SCROLL_UP_COEFFICIENT)
            headerAnim.start()

        } else {
            val headerAnim = provideHeaderScrollAnimation(AuthConstants.HEADER_SCROLL_DOWN_COEFFICIENT)
            headerAnim.start()
        }
    }

    override fun onStart() {
        super.onStart()
        updateEmailField()
        updatePhoneNumber()
        updateAddressField()
        updateCity()
        updateZipCode()
        updateCountry()
        phoneNumberSubscription()
        addressSubscription()
        citySubscription()
        zipCodeSubscription()
        countrySubscription()
        subscribeToUpdateInfoButton()
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
        /*sharedViewModel.userEmail
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_email_address.setText(it)
                et_email_address.isEnabled = false
            }.let { disposables.add(it) }*/
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
    private fun updateAddressField() {
        sharedViewModel.userAddressLine
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_address_line.setText(it)
            }.let { disposables.add(it) }
    }

    private fun updateCity() {
        sharedViewModel.userCity
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_city.setText(it)
            }.let { disposables.add(it) }
    }

    private fun updateZipCode() {
        sharedViewModel.userZipCode
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_zip_code.setText(it)
            }.let { disposables.add(it) }
    }

    private fun updateCountry() {
        sharedViewModel.userCountry
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (et_country.text.toString() != it) {
                    et_country.tag = "SET"
                    et_country.setText(it)
                }
            }.let { disposables.add(it) }
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

    private fun addressSubscription() {
        et_address_line.textChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                sharedViewModel.userAddressLine.onNext("$it")
            }.let { disposables.add(it) }
    }

    private fun citySubscription() {
        et_city.textChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                sharedViewModel.userCity.onNext("$it")
            }.let { disposables.add(it) }
    }

    private fun zipCodeSubscription() {
        et_zip_code.textChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                sharedViewModel.userZipCode.onNext("$it")
            }.let { disposables.add(it) }
    }

    private fun countrySubscription() {
        et_country.textChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (et_country.tag == null) {
                    sharedViewModel.userCountry.onNext("$it")
                }
                et_country.tag = null
            }.let { disposables.add(it) }
    }

    private fun subscribeToUpdateInfoButton() {
        btn_update_contact_info.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                sharedViewModel.finishRegistration()
            }. let { disposables.add(it) }
    }

    //region Animation functions
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

    private fun provideHeaderScrollAnimation(heightCoefficient: Float): ValueAnimator {
        val anim = ValueAnimator.ofInt(
            headerView.measuredHeight,
            (headerView.measuredHeight * heightCoefficient).toInt()
        )
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            val params = headerView.layoutParams
            params.height = value
            headerView.layoutParams = params

        }
        anim.interpolator = AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear)
        anim.duration = 200L
        return anim
    }
    //endregion
}