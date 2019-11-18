package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import io.reactivex.subjects.BehaviorSubject

interface ViewModelContract {

    val userEmail: BehaviorSubject<String>
    val userPassword: BehaviorSubject<String>

    val userFirstName: BehaviorSubject<String>
    val userSurname: BehaviorSubject<String>
    val userNickname: BehaviorSubject<String>

    fun startUserVerification(flag: String)
    fun startUserRegistration()
    fun finishRegistration()

    fun startUserLogin()

}