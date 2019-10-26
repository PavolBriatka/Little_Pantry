package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import io.reactivex.subjects.BehaviorSubject

interface ViewModelContract {

    val loginEmail: BehaviorSubject<String>
    val loginPassword: BehaviorSubject<String>
    val registerEmail: BehaviorSubject<String>
    val registerPassword: BehaviorSubject<String>

    val userFirstName: BehaviorSubject<String>
    val userSurname: BehaviorSubject<String>
    val userNickname: BehaviorSubject<String>

    fun startUserVerification(flag: String)
    fun startUserRegistration()
    fun finishRegistration()

    fun startUserLogin()

}