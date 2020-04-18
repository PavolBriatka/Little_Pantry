package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.models.DisplayableUser
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.HEADER_SCROLL_DOWN_COEFFICIENT
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.HEADER_SCROLL_UP_COEFFICIENT
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.shuhart.stepview.StepView
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function5
import kotlinx.android.synthetic.main.fragment_registration_form.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RegistrationFormFragment : DaggerFragment() {


    private val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()
    
    private lateinit var headerView: StepView
    private lateinit var mContext: Context

    @Inject
    lateinit var steps: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mContext = requireContext()
        return inflater.inflate(R.layout.fragment_registration_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView = view.findViewById(R.id.sv_registration_header)

        headerView.setSteps(steps)
        animateViewsIn(view)

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
            val headerAnim = provideHeaderScrollAnimation(HEADER_SCROLL_UP_COEFFICIENT)
            headerAnim.start()

        } else {
            val headerAnim = provideHeaderScrollAnimation(HEADER_SCROLL_DOWN_COEFFICIENT)
            headerAnim.start()
        }
    }

    override fun onStart() {
        super.onStart()
        updateFormFields()
        subscribeToFormFields()
        subscribeToRegisterButton()
        userEmits()
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    private fun updateFormFields() {
        sharedViewModel.displayableUser
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                et_user_first_name.setText(it.uFirstName)
                et_user_surname.setText(it.uSurname)
                et_user_nickname.setText(it.uNickname)
                et_email_address.setText(it.uEmail)
                et_email_address.isEnabled = false
                et_register_password.setText(it.uPassword)
            }.let { disposables.add(it) }
    }

    private fun userEmits() {
        sharedViewModel.displayableUser
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("uFirstName", "${it.uFirstName}")
                Log.e("uSurname", "${it.uSurname}")
                Log.e("uNickname", "${it.uNickname}")
                Log.e("uEmail", "${it.uEmail}")
                Log.e("uPassword", "${it.uPassword}")


            }.let { disposables.add(it) }
    }

    private fun subscribeToFormFields() {
        Observable.combineLatest(sharedViewModel.displayableUser,
            et_user_first_name.textChanges(),
            et_user_surname.textChanges(),
            et_user_nickname.textChanges(),
            et_register_password.textChanges(),
            Function5<DisplayableUser, CharSequence, CharSequence, CharSequence, CharSequence, DisplayableUser> { user, firstName, surname, nickname, password ->
                user.copy(
                    uFirstName = firstName.toString(),
                    uSurname = surname.toString(),
                    uNickname = nickname.toString(),
                    uPassword = password.toString()
                )
            })
            /* Buffer to accumulate values that need to be compared in filter().
            * Otherwise, this would create an infinite loop of user updates
            * that would end the world as we know it!!!*/
            .buffer(2)
            .filter { lastTwoEmissions ->
                lastTwoEmissions[0] != lastTwoEmissions[1]
            }
            .subscribe { unequalEmissions ->
                sharedViewModel.displayableUser.onNext(unequalEmissions[1])
            }.let { disposables.add(it) }
    }

    private fun subscribeToRegisterButton() {
        btn_create_new_user.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                sharedViewModel.startUserRegistration()
            }.let { disposables.add(it) }
    }

    //region Animation functions
    private fun animateViewsIn(view: View) {
        val root = view.findViewById<ConstraintLayout>(R.id.cl_registration_form)
        val count = root.childCount
        var offset = resources.getDimensionPixelSize(R.dimen.offset_y).toFloat()
        val interpolator =
            AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear_out_slow_in)

        for (i in 0 until count) {
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
