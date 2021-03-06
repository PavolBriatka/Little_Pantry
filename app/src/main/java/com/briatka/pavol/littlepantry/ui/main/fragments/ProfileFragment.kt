package com.briatka.pavol.littlepantry.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.main.viewmodel.MainViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : DaggerFragment() {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val disposables = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        disposables.addAll(
            subscribeToTransitionProgress(),
            subscribeToProfilePhoto(),
            subscribeToUserData()
        )
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    private fun subscribeToTransitionProgress(): Disposable {
        return sharedViewModel.transitionProgress.hide()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                ml_profile_body.progress = it
                /**Make views visible only after at least half of the fragment is visible
                 * otherwise the views are "squeezed" in the fragment as it opens.*/
                if (it >= 0.5 ) {
                    val mAlpha =  ((it - 0.5) * 2).toFloat()
                    tl_profile_name.alpha =  mAlpha
                    tl_profile_nickname.alpha = mAlpha
                    tl_profile_email_address.alpha = mAlpha
                }
            }
    }

    private fun subscribeToProfilePhoto(): Disposable {
        return sharedViewModel.profilePhoto.hide()
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                civ_profile_photo.setImageBitmap(it)
            }
    }

    private fun subscribeToUserData(): Disposable {
        return sharedViewModel.userData.hide()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { userData ->
                val fullName = "${userData.firstName} ${userData.surname}"
                et_profile_name.setText(fullName)
                et_profile_nickname.setText(userData.nickname)
                et_profile_email_address.setText(userData.email)
            }
    }
}
