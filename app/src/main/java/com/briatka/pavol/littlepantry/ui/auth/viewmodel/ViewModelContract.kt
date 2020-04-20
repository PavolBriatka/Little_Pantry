package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.graphics.Bitmap
import com.briatka.pavol.littlepantry.models.DisplayableUser
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ViewModelContract {

    val displayableUser: BehaviorSubject<DisplayableUser>

    val userProfilePhotoState: PublishSubject<ProfilePictureState>
    val permissionIntentDispatcher: PublishSubject<Int>

    fun updateUserPhoneNumber(phoneNumber: String)
    fun updateUserCountry(country: String)

    fun startUserVerification(flag: String)
    fun startUserRegistration()
    fun finishRegistration()

    fun startUserLogin()

    fun uploadUserProfilePicture()
    fun updateUserProfilePicture(newPicture: Bitmap)

}