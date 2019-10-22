package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.LoginPagerAdapter
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState.*
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_login.*


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

    override fun onDestroy() {
        pagerAdapter.dispose()
        super.onDestroy()
    }
}
