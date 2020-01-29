package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.ProfilePictureState.ProfilePictureError
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.FILE_PROVIDER_AUTHORITY
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.PERMISSION_REQUEST_CAMERA
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REQUEST_IMAGE_CAPTURE
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REQUEST_PICK_IMAGE_FROM_GALLERY
import com.briatka.pavol.littlepantry.utils.PhotoUtils
import com.jakewharton.rxbinding3.view.clicks
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_user_profile_picture.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class UserProfilePictureFragment : DaggerFragment() {


    private val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()
    private var tempFilePath: String? = null
    private var profilePicture: Bitmap? = null
    @Inject
    lateinit var steps: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_profile_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateViewsIn(view)
        sv_registration_header.setSteps(steps)
        /**
         * Setup visibility observer so the app can run the animation when the view is
         * actually VISIBLE. It's important to remove the listener so it's only called once -
         * otherwise the app would crash.
         * */
        val treeObserver = sv_registration_header.viewTreeObserver
        treeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                treeObserver.removeOnGlobalLayoutListener(this)
                if (sv_registration_header.visibility == View.VISIBLE) {
                    sv_registration_header.go(1, true)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        subscribeToUpdateProfileButton()
        subscribeToTakePhotoButton()
        subscribeToPickFromGalleryButton()
        subscribeToProfilePictureState()
        subscribeToRotateRightButton()
        subscribeToRotateLeftButton()
        subscribeToProfilePhoto()
    }

    private fun subscribeToProfilePictureState() {
        sharedViewModel.userProfilePhotoState.hide()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                when (state) {
                    is ProfilePictureError -> {
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    }
                }
            }.let { disposables.add(it) }
    }

    override fun onStop() {
        tempFilePath?.let {
            PhotoUtils.deleteFileFromCache(tempFilePath!!)
        }
        disposables.clear()
        super.onStop()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    processImageFromCamera()
                } else if (resultCode != Activity.RESULT_CANCELED) {
                    sharedViewModel.userProfilePhotoState.onNext(
                        ProfilePictureError(getString(R.string.error_processing_profile_picture))
                    )
                }
            }
            REQUEST_PICK_IMAGE_FROM_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    processImageFromGallery(data?.data)
                } else if (resultCode != Activity.RESULT_CANCELED) {
                    sharedViewModel.userProfilePhotoState.onNext(
                        ProfilePictureError(getString(R.string.error_processing_profile_picture))
                    )
                }
            }
        }
    }

    private fun subscribeToPickFromGalleryButton() {
        iv_select_picture.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {

                tempFilePath?.let {
                    PhotoUtils.deleteFileFromCache(tempFilePath!!)
                }

                if (checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
                    )
                } else {
                    openGallery()
                }
            }.let { disposables.add(it) }
    }

    private fun subscribeToTakePhotoButton() {
        iv_take_picture.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {

                tempFilePath?.let {
                    PhotoUtils.deleteFileFromCache(tempFilePath!!)
                }

                if (checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSION_REQUEST_CAMERA
                    )
                } else {
                    dispatchTakePictureIntent()
                }
            }.let { disposables.add(it) }
    }

    private fun subscribeToProfilePhoto() {
        sharedViewModel.userProfilePhoto.hide()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                civ_profile_photo.setImageBitmap(it)
                profilePicture = it
            }.let { disposables.add(it) }
    }

    private fun subscribeToRotateRightButton() {
        iv_rotate_right.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                profilePicture?.let {
                    val animation =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_right)
                    animation.setAnimationListener(listenerAnim())
                    civ_profile_photo.startAnimation(animation)
                    //sharedViewModel.userProfilePhoto.onNext(PhotoUtils.rotateRight(profilePicture!!))
                }

            }.let { disposables.add(it) }
    }

    private fun listenerAnim(): Animation.AnimationListener {
        return object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                //sharedViewModel.userProfilePhoto.onNext(PhotoUtils.rotateRight(profilePicture!!))
                //civ_profile_photo.setImageBitmap(PhotoUtils.rotateRight(profilePicture!!))
            }

            override fun onAnimationStart(animation: Animation?) {}
        }
    }

    private fun subscribeToRotateLeftButton() {
        iv_rotate_left.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                profilePicture?.let {
                    sharedViewModel.userProfilePhoto.onNext(PhotoUtils.rotateLeft(profilePicture!!))
                }

            }.let { disposables.add(it) }
    }

    private fun subscribeToUpdateProfileButton() {
        btn_update_profile.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                sharedViewModel.uploadUserProfilePicture()
            }.let { disposables.add(it) }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_PICK_IMAGE_FROM_GALLERY)
    }

    private fun processImageFromGallery(uri: Uri?) {
        if (uri != null) {
            tempFilePath = PhotoUtils.getPathFromUri(requireContext(), uri)
            if (tempFilePath != null) {
                sharedViewModel.userProfilePhoto.onNext(
                    PhotoUtils.fixRotation(
                        PhotoUtils.resampleImage(tempFilePath!!, requireContext()), tempFilePath!!
                    )
                )
            } else {
                sharedViewModel.userProfilePhotoState.onNext(
                    ProfilePictureError(getString(R.string.unsupported_picture_format))
                )
            }
        } else {
            sharedViewModel.userProfilePhotoState.onNext(
                ProfilePictureError(getString(R.string.error_processing_profile_picture))
            )
        }
    }

    private fun dispatchTakePictureIntent() {

        var tempFile: File? = null
        try {
            tempFile = PhotoUtils.createTempFile(requireContext())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        tempFile?.let {
            tempFilePath = it.absolutePath
            val pictureUri =
                FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, tempFile)

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun processImageFromCamera() {

        sharedViewModel.userProfilePhoto.onNext(
            PhotoUtils.fixRotation(
                PhotoUtils.resampleImage(tempFilePath!!, requireContext()), tempFilePath!!
            )
        )
    }

    private fun animateViewsIn(view: View) {
        val root = view.findViewById<ConstraintLayout>(R.id.cl_root)
        val count = root.childCount
        var offset = resources.getDimensionPixelSize(R.dimen.offset_y).toFloat()
        val interpolator =
            AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in)

        for (i in 1 until count) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else {
                    //TODO: fix
                }
            READ_EXTERNAL_STORAGE_PERMISSION_REQUEST ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    //TODO: check with PO what should be done in this case
                    Toast.makeText(
                        requireContext(),
                        "Can't open gallery without permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}
