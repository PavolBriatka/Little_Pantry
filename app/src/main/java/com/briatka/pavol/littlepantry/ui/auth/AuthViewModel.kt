package com.briatka.pavol.littlepantry.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth): ViewModel() {

    companion object {
        const val TAG = "AuthViewModel"
    }

    init {
        Log.e(TAG, "view model init...")
    }
}