package com.briatka.pavol.littlepantry.ui.auth.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.briatka.pavol.littlepantry.R
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REQUEST_IMAGE_CAPTURE
import com.briatka.pavol.littlepantry.utils.PhotoUtils
import com.jakewharton.rxbinding3.view.clicks
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_register_user_profile.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class RegisterUserProfileFragment : DaggerFragment() {

    companion object {
        private const val FILE_PROVIDER_AUTHORITY = "com.briatka.pavol.fileprovider"
    }

    private val sharedViewModel: AuthViewModel by activityViewModels()
    private val disposables = CompositeDisposable()
    private lateinit var tempFilePath: String
    private lateinit var profilePicture: Bitmap

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                processImage()
            }
        }
    }

    private fun processImage() {

        profilePicture = PhotoUtils.resampleImage(tempFilePath, requireContext())
        val resizedPicture = ThumbnailUtils.extractThumbnail(profilePicture, 200, 200)
        civ_profile_photo.setImageBitmap(resizedPicture)
    }

    private fun subscribeToTakePhotoButton() {
        iv_take_picture.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                dispatchTakePictureIntent()
            }.let { disposables.add(it) }
    }

    private fun subscribeToUpdateProfileButton() {
        btn_update_profile.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                //TODO: redirect to overview screen
            }.let { disposables.add(it) }
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
            val pictureUri = FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, tempFile)

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }



    }
}
