package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth) : ViewModel(),
    ViewModelContract {

    companion object {
        const val TAG = "AuthViewModel"
    }

    val isExistingUser = MutableLiveData<Boolean>()

    init {
        Log.e(TAG, "view model init...")
    }

    override fun startUserVerification(email: String) {
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