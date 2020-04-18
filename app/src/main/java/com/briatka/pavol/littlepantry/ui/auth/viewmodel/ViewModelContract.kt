package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.graphics.Bitmap
import com.briatka.pavol.littlepantry.models.DisplayableUser
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ViewModelContract {

    val userProfilePhoto: BehaviorSubject<Bitmap>
    val userPhoneNumber: BehaviorSubject<String>
    val userAddressLine: BehaviorSubject<String>
    val userCity: BehaviorSubject<String>
    val userZipCode: BehaviorSubject<String>
    val userCountry: BehaviorSubject<String>

    val displayableUser: BehaviorSubject<DisplayableUser>

    val userProfilePhotoState: PublishSubject<ProfilePictureState>
    val permissionIntentDispatcher: PublishSubject<Int>

    fun startUserVerification(flag: String)
    fun startUserRegistration()
    fun finishRegistration()

    fun startUserLogin()

    fun uploadUserProfilePicture()

}