package com.briatka.pavol.littlepantry.ui.main.viewmodel

import android.graphics.Bitmap
import com.briatka.pavol.littlepantry.models.UserContactData
import com.briatka.pavol.littlepantry.models.MandatoryDbUserData
import io.reactivex.subjects.BehaviorSubject

interface ViewModelContract {

    val userData: BehaviorSubject<MandatoryDbUserData>
    val contactInfoData: BehaviorSubject<UserContactData>
    val profilePhoto: BehaviorSubject<Bitmap>

    fun fetchUserData()
    fun logout()
}