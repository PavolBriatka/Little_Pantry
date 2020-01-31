package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.graphics.Bitmap
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ViewModelContract {

    val userEmail: BehaviorSubject<String>
    val userPassword: BehaviorSubject<String>

    val userFirstName: BehaviorSubject<String>
    val userSurname: BehaviorSubject<String>
    val userNickname: BehaviorSubject<String>
    val userProfilePhoto: BehaviorSubject<Bitmap>
    val userProfilePhotoState: PublishSubject<ProfilePictureState>
    val userPhoneNumber: BehaviorSubject<String>
    val userAddressLine: BehaviorSubject<String>
    val userCity: BehaviorSubject<String>
    val userZipCode: BehaviorSubject<String>
    val userCountry: BehaviorSubject<String>

    fun startUserVerification(flag: String)
    fun startUserRegistration()
    fun finishRegistration()

    fun startUserLogin()

    fun uploadUserProfilePicture()

}