package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_register_user_profile.*


class RegisterUserProfileFragment : DaggerFragment() {

    val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_next_page.setOnClickListener {
            sharedViewModel.finishRegistration()
        }
    }
}
