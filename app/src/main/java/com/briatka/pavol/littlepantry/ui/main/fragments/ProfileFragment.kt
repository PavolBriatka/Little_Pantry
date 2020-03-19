package com.briatka.pavol.littlepantry.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

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
            subscribeToProfilePhoto()
        )
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    private fun subscribeToProfilePhoto(): Disposable {
        return sharedViewModel.profilePhoto.hide()
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                civ_profile_photo.setImageBitmap(it)
            }
    }
}
