package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.briatka.pavol.littlepantry.models.DisplayableUser
import com.briatka.pavol.littlepantry.models.MandatoryDbUserData
import com.briatka.pavol.littlepantry.models.UserContactData
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.UserState.*
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.CONTACT_INFO_PATH
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_EMAIL_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.PROFILE_PHOTO_REFERENCE
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.PROFILE_PHOTO_REFERENCE_PATH
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.USERS_PATH
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function5
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : ViewModel(),
    ViewModelContract {

    companion object {
        const val TAG = "AuthViewModel"
        const val ERROR_UNSPECIFIED = "error_unspecified"
    }

    private val disposables = CompositeDisposable()
    val userState = MutableLiveData<UserState>()

    override val displayableUser: BehaviorSubject<DisplayableUser>
            = BehaviorSubject.createDefault(DisplayableUser())

    override val userPhoneNumber: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    override val userAddressLine: BehaviorSubject<String> = BehaviorSubject.create()
    override val userCity: BehaviorSubject<String> = BehaviorSubject.create()
    override val userZipCode: BehaviorSubject<String> = BehaviorSubject.create()
    override val userCountry: BehaviorSubject<String> = BehaviorSubject.create()

    override val userProfilePhotoState: PublishSubject<ProfilePictureState> = PublishSubject.create()
    /**
     * The clicks to take a picture or upload image from gallery are registered in a Fragment
     * but the permissions to access Camera/Gallery are handled in the Activity and thus we can
     * use this Subject to notify the Fragment from Activity when the permissions are granted*/
    override val permissionIntentDispatcher: PublishSubject<Int> = PublishSubject.create()


    override fun startUserVerification(flag: String) {
        userState.postValue(AuthInProgress(flag))
        displayableUser
            .firstElement()
            .subscribe {
                verifyUserEmail(it.uEmail, flag)
            }.let { disposables.add(it) }


    }

    private fun verifyUserEmail(email: String, flag: String) {
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener { result ->
            val signInMethods = result.signInMethods
            when (flag) {
                LOGIN_FLAG -> {
                    signInMethods?.let {
                        if (it.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                            startUserLogin()
                        } else {
                            userState.postValue(AuthUserNotExist)
                        }
                    }
                }
                REGISTER_FLAG -> {
                    signInMethods?.let {
                        if (it.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                            userState.postValue(UserAlreadyExist)
                        } else {
                            userState.postValue(CreateNewUser)
                        }
                    }
                }
            }
        }
            .addOnFailureListener {
                userState.postValue(AuthEmailVerificationFailure(LOGIN_EMAIL_ERROR))
            }
    }

    override fun startUserRegistration() {

        userState.postValue(StartUserRegistration)

        displayableUser
            .firstElement()
            .subscribe { user ->
                registerNewUser(user.uEmail, user.uPassword)
            }.let { disposables.add(it) }
    }

    private fun registerNewUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateDbProfileInfo()
                } else {
                    //TODO: Create a general error message in case the exception does not provide one
                    userState.postValue(DataUpdateFailed(task.exception?.message ?: ERROR_UNSPECIFIED))
                }
            }
    }

    private fun updateDbProfileInfo() {

        firebaseAuth.currentUser?.uid?.let { userId ->
            val dbUserReference = firestoreDb.collection(USERS_PATH).document(userId)

            displayableUser
                .map { user ->
                    MandatoryDbUserData(user.uFirstName, user.uSurname, user.uNickname, user.uEmail)
                }
                .firstElement()
                .subscribe { newUser ->
                    dbUserReference.set(newUser)
                        .addOnSuccessListener { userState.postValue(SetUserProfilePicture) }
                        .addOnFailureListener { Log.w(TAG, it.message) }
                }.let { disposables.add(it) }
        }
    }

    override fun updateUserProfilePicture(newPicture: Bitmap) {
        displayableUser
        .firstElement()
        .subscribe { user ->
            displayableUser.onNext(user.copy(uProfilePhoto = newPicture))
        }.dispose()
    }

    override fun uploadUserProfilePicture() {
        userState.postValue(UploadUserProfilePicture)

        firebaseAuth.currentUser?.uid?.let { uid ->
            displayableUser.hide()
                .firstElement()
                .subscribe { user ->

                    if (user.uProfilePhoto != null) {

                        val fileName = "${System.currentTimeMillis()}.bmp"
                        val storageReference =
                            firebaseStorage.reference.child("$uid/profile_photo/$fileName")

                        storageReference.putBytes(preparePicture(user.uProfilePhoto!!))
                            .addOnSuccessListener {
                                savePhotoName(fileName)
                                userState.postValue(CollectContactInfo)
                            }
                            .addOnFailureListener {
                                userState.postValue(
                                    DataUpdateFailed(
                                        it.message ?: ERROR_UNSPECIFIED
                                    )
                                )
                            }
                    } else {
                        userState.postValue(CollectContactInfo)
                    }

                }.let { disposables.add(it) }
        }
    }

    private fun savePhotoName(fileName: String) {
        firebaseAuth.currentUser?.uid?.let { userId ->
            val dbReference = firestoreDb.collection(USERS_PATH)
                .document(userId)
                .collection(PROFILE_PHOTO_REFERENCE_PATH).document(userId)

            dbReference.set(mapOf(Pair(PROFILE_PHOTO_REFERENCE, fileName)))
        }
    }

    private fun preparePicture(profilePicture: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        profilePicture.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()

    }

    override fun finishRegistration() {

        userState.postValue(SubmitContactInfo)

        firebaseAuth.currentUser?.uid?.let { userId ->
            val dbReference =
                firestoreDb.collection(USERS_PATH).document(userId).collection(CONTACT_INFO_PATH)
                    .document(userId)

            Observable.combineLatest(userPhoneNumber.hide(),
                userAddressLine.hide(),
                userCity.hide(),
                userZipCode.hide(),
                userCountry.hide(),
                Function5<String, String, String, String, String, UserContactData> { phoneNo, addressLine, city, zipCode, country ->
                    UserContactData(phoneNo, addressLine, city, zipCode, country)
                })
                .firstElement()
                .subscribe { contactData ->
                    dbReference.set(contactData)
                        .addOnSuccessListener {
                            userState.postValue(RegistrationFinalized)
                        }
                        //TODO: Create a general error message in case the exception does not provide one
                        .addOnFailureListener {
                            userState.postValue(
                                DataUpdateFailed(
                                    it.message ?: ERROR_UNSPECIFIED
                                )
                            )
                        }
                }.let { disposables.add(it) }
        }
    }

    override fun startUserLogin() {
        displayableUser
            .firstElement()
            .subscribe { user ->
                loginUser(user.uEmail, user.uPassword)
            }.let { disposables.add(it) }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    userState.postValue(AuthLoginSuccessful)
                } else {
                    Log.e(TAG, "signInWithEmail:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        userState.postValue(AuthWrongPassword)
                    } else {
                        userState.postValue(
                            AuthLoginFailure(task.exception?.message ?: ERROR_UNSPECIFIED)
                        )
                    }
                }
            }
    }

}