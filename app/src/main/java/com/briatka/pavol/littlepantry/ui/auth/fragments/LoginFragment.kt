package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.LoginPagerAdapter
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState.*
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_PASSWORD_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_USER_NOT_EXIST
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_USER_ALREADY_EXIST
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment


class LoginFragment : DaggerFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: LoginPagerAdapter

    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.vp_login_layout)
        pagerAdapter = LoginPagerAdapter(requireContext(), sharedViewModel)

        viewPager.adapter = pagerAdapter
    }

    override fun onStart() {
        super.onStart()
        subscribeObserver()
    }

    override fun onDestroy() {
        pagerAdapter.dispose()
        super.onDestroy()
    }

    private fun subscribeObserver() {
        sharedViewModel.authState.removeObservers(viewLifecycleOwner)
        sharedViewModel.authState.observe(viewLifecycleOwner, Observer { userState ->
            when (userState) {
                is AuthEmailVerificationFailure -> {
                    pagerAdapter.onCredentialsError(userState.error)
                }
                AuthUserNotExist -> {
                    pagerAdapter.onCredentialsError(LOGIN_USER_NOT_EXIST)
                }
                AuthUserAlreadyExist -> {
                    pagerAdapter.onCredentialsError(REGISTER_USER_ALREADY_EXIST)
                }
                is AuthLoginFailure -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.login_error_unknown),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                AuthWrongPassword -> {
                    pagerAdapter.onCredentialsError(LOGIN_PASSWORD_ERROR)
                }
            }
        })
    }
}
