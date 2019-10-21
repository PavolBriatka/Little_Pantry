package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import io.reactivex.subjects.BehaviorSubject

interface ViewModelContract {

    val loginEmail: BehaviorSubject<String>
    val loginPassword: BehaviorSubject<String>

    fun startUserVerification()

}