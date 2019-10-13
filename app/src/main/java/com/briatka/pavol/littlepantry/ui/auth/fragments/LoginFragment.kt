package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.LoginPagerAdapter
import dagger.android.support.DaggerFragment


class LoginFragment : DaggerFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var  pagerAdapter: LoginPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root =  inflater.inflate(R.layout.fragment_login, container, false)

        viewPager = root.findViewById(R.id.login_viewpager)
        pagerAdapter = LoginPagerAdapter(requireContext())

        viewPager.adapter = pagerAdapter

        return root
    }


}
