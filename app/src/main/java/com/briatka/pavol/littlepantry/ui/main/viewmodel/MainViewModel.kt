package com.briatka.pavol.littlepantry.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDb: FirebaseFirestore,
    val firebaseAuthLiveData: FirebaseAuthLiveData
) : ViewModel(),
    ViewModelContract {


    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        Log.d(TAG, "MainViewModel init...")
    }


    override fun logout() {
        firebaseAuth.signOut()
    }
}