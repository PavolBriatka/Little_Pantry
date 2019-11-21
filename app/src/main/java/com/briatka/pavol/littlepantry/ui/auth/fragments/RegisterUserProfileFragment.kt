package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_register_user_profile.*


class RegisterUserProfileFragment : DaggerFragment() {

    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Setup visibility observer so the app can run the animation when the view is
         * actually VISIBLE. It's important to remove the listener so it's only called once -
         * otherwise the app would crash.
         * */
        val treeObserver = sv_registration_header.viewTreeObserver
        treeObserver.addOnGlobalLayoutListener( object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                treeObserver.removeOnGlobalLayoutListener(this)
                if (sv_registration_header.visibility == View.VISIBLE) {
                    sv_registration_header.go(1, true)
                }
            }
        })

        Log.e("VIEW", "isThisFirst?")
        btn_next_page.setOnClickListener {
            sharedViewModel.finishRegistration()
        }
    }


}
