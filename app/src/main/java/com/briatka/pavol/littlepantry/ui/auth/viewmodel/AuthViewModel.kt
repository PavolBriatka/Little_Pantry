package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth) : ViewModel(),
    ViewModelContract {

    companion object {
        const val TAG = "AuthViewModel"
    }

    private val disposables = CompositeDisposable()
    val isExistingUser = MutableLiveData<Boolean>()

    override val loginEmail: BehaviorSubject<String> = BehaviorSubject.create()
    override val loginPassword: BehaviorSubject<String> = BehaviorSubject.create()

    init {
        Log.e(TAG, "view model init...")
    }

    override fun startUserVerification() {
        Observable.combineLatest(loginEmail.hide(), loginPassword.hide(),
            BiFunction<String, String, Pair<String, String>> { email, password ->
                Pair(email,password)
            })
            .firstElement()
            .subscribe {
            verifyUserEmail(it.first)
        }.let { disposables.add(it) }
    }

    private fun verifyUserEmail(email: String) {
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener { result ->
            val signInMethods = result.signInMethods
            isExistingUser.postValue(signInMethods!!.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD))
        }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message)
                isExistingUser.postValue(false)
            }
    }
}