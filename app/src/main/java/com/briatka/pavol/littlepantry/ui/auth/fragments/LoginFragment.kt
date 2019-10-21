package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.LoginPagerAdapter
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable


class LoginFragment : DaggerFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: LoginPagerAdapter

    val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.login_viewpager)
        pagerAdapter = LoginPagerAdapter(requireContext(), sharedViewModel)

        viewPager.adapter = pagerAdapter
    }

    override fun onStart() {
        super.onStart()
        subscribeObserver()

    }

    private fun subscribeObserver() {
        sharedViewModel.isExistingUser.observe(this, Observer<Boolean> { isExistingUser ->
            Toast.makeText(requireContext(), "Is existing user: $isExistingUser", Toast.LENGTH_LONG)
                .show()
        })
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onDestroy() {
        disposables.dispose()
        pagerAdapter.dispose()
        super.onDestroy()
    }
}
