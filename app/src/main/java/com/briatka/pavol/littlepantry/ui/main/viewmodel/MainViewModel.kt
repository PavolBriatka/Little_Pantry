package com.briatka.pavol.littlepantry.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : ViewModel(),
    ViewModelContract {


    override fun logout() {
        firebaseAuth.signOut()
    }
}