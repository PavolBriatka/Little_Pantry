package com.briatka.pavol.littlepantry.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FirebaseAuthLiveData @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    LiveData<FirebaseUser>() {

    companion object {
        private const val TAG = "FirebaseAuthLiveData"
    }

    init {
        Log.e(TAG, "init...")
    }

    private val authStateListener =
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            value = user
        }

    override fun onActive() {
        super.onActive()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        super.onInactive()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}