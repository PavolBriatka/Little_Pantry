package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.LoginPagerAdapter
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable


class LoginFragment : DaggerFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var  pagerAdapter: LoginPagerAdapter
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
        pagerAdapter = LoginPagerAdapter(requireContext())
        pagerAdapter.onButtonClicked =  {
            this.findNavController().navigate(if (it.id == R.id.btn_login_email_password) {
                R.id.action_authenticate_user
            } else {
                R.id.action_start_registration
            })
        }

        viewPager.adapter = pagerAdapter
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
