package com.briatka.pavol.littlepantry.ui.main.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.briatka.pavol.littlepantry.models.UserContactData
import com.briatka.pavol.littlepantry.models.MandatoryDbUserData
import com.briatka.pavol.littlepantry.utils.AuthConstants
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.CONTACT_INFO_PATH
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.PROFILE_PHOTO_REFERENCE_PATH
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.USERS_PATH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : ViewModel(),
    ViewModelContract {

    override val userData: BehaviorSubject<MandatoryDbUserData> = BehaviorSubject.create()
    override val contactInfoData: BehaviorSubject<UserContactData> = BehaviorSubject.create()
    override val profilePhoto: BehaviorSubject<Bitmap> = BehaviorSubject.create()
    val transitionProgress: BehaviorSubject<Float> = BehaviorSubject.create()

    override fun fetchUserData() {
        firebaseAuth.currentUser?.let { user ->
            val userDataRef = firestoreDb.collection(USERS_PATH).document(user.uid)
            val contactInfoRef = userDataRef.collection(CONTACT_INFO_PATH).document(user.uid)
            val profilePhotoRef = userDataRef.collection(PROFILE_PHOTO_REFERENCE_PATH).document(user.uid)

            fetchBasicData(userDataRef)
            fetchContactInfo(contactInfoRef)
            fetchProfilePhotoReference(user.uid, profilePhotoRef)
        }
    }

    private fun fetchProfilePhotoReference(uid: String, profilePhotoRef: DocumentReference) {
        profilePhotoRef.get()
            .addOnSuccessListener { snapshot ->
                snapshot.get(AuthConstants.PROFILE_PHOTO_REFERENCE, String::class.java)?.let {
                    fetchProfileImage(uid, it)
                }
            }
    }

    private fun fetchProfileImage(uid: String, path: String) {
        val storageRef = firebaseStorage.reference.child("$uid/profile_photo/$path")

        storageRef.getBytes(1024 * 10240)
            .addOnSuccessListener {
            profilePhoto.onNext(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
            .addOnFailureListener {
                Log.w("MAIN_VIEW_MODEL", it.message)
        }
    }

    private fun fetchContactInfo(contactInfoRef: DocumentReference) {
        contactInfoRef.get()
            .addOnSuccessListener {
                it.toObject(UserContactData::class.java)?.let { data ->
                    contactInfoData.onNext(data)
                }
            }

    }

    private fun fetchBasicData(userDataRef: DocumentReference) {
        userDataRef.get()
            .addOnSuccessListener {
                it.toObject(MandatoryDbUserData::class.java)?.let { data ->
                    userData.onNext(data)
                }

            }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}