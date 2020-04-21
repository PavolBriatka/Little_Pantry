package com.briatka.pavol.littlepantry

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import com.briatka.pavol.littlepantry.models.DisplayableUser
import com.briatka.pavol.littlepantry.models.MandatoryDbUserData
import com.briatka.pavol.littlepantry.models.UserContactData
import com.briatka.pavol.littlepantry.utils.AuthConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class DisplayableUserLiveData @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : LiveData<DisplayableUser>() {

    private val tag = "DisplayableUserLiveData"

    private var displayableUser = DisplayableUser()
    private val disposable: CompositeDisposable = CompositeDisposable()
    private val dataCompletionListener: PublishSubject<Boolean> = PublishSubject.create()

    override fun onActive() {
        super.onActive()
        if (hasUserData()) {
            Log.e(tag, "hasUserData")
            value = displayableUser
        } else {
            Log.e(tag, "hasNOTUserData")
            subscribeToCompletionListener()
            fetchUserData()
        }
    }

    private fun subscribeToCompletionListener() {
        dataCompletionListener.hide()
            .scan(0, { count, _ -> count + 1 })
            .filter { it == 3 }
            .subscribe {
                value = displayableUser
            }.let { disposable.add(it) }
    }

    private fun fetchUserData() {
        firebaseAuth.currentUser?.let { user ->
            val userDataRef = firestoreDb.collection(AuthConstants.USERS_PATH).document(user.uid)
            val contactInfoRef =
                userDataRef.collection(AuthConstants.CONTACT_INFO_PATH).document(user.uid)
            val profilePhotoRef = userDataRef.collection(AuthConstants.PROFILE_PHOTO_REFERENCE_PATH)
                .document(user.uid)

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
                displayableUser = displayableUser.copy(
                    uProfilePhoto = BitmapFactory.decodeByteArray(
                        it,
                        0,
                        it.size
                    )
                )
                Log.e(tag, "profileImage - onSuccess")

                dataCompletionListener.onNext(true)
            }
            .addOnFailureListener {
                if (BuildConfig.DEBUG)
                    Log.w("MAIN_VIEW_MODEL", it.message)
            }
    }

    private fun fetchContactInfo(contactInfoRef: DocumentReference) {
        contactInfoRef.get()
            .addOnSuccessListener {
                it.toObject(UserContactData::class.java)?.let { data ->
                    displayableUser = displayableUser.copy(
                        uPhoneNumber = data.phoneNumber,
                        uAddressLine = data.addressLine,
                        uCity = data.city,
                        uZipCode = data.zipCode,
                        uCountry = data.country
                    )
                    Log.e(tag, "Contact info - on success")

                    dataCompletionListener.onNext(true)
                }
            }

    }

    private fun fetchBasicData(userDataRef: DocumentReference) {
        userDataRef.get()
            .addOnSuccessListener {
                it.toObject(MandatoryDbUserData::class.java)?.let { data ->
                    displayableUser = displayableUser.copy(
                        uFirstName = data.firstName,
                        uSurname = data.surname,
                        uNickname = data.nickname,
                        uEmail = data.email
                    )
                    Log.e(tag, "basic data on success")

                    dataCompletionListener.onNext(true)
                }

            }
    }

    /**
     * Check if we already have the data. We are checking on:
     * e-mail, first name, last name and nick name as these are mandatory
     * during registration. All other values are optional*/
    private fun hasUserData(): Boolean {
        return displayableUser.uEmail.isNotBlank() &&
                displayableUser.uFirstName.isNotBlank() &&
                displayableUser.uSurname.isNotBlank() &&
                displayableUser.uNickname.isNotBlank()
    }

    override fun onInactive() {
        super.onInactive()
        disposable.clear()
    }
}