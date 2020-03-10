package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.briatka.pavol.littlepantry.models.NewUser
import com.briatka.pavol.littlepantry.models.UserContactData
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.UserState.*
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_EMAIL_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_FLAG
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
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

    override val userEmail: BehaviorSubject<String> = BehaviorSubject.create()
    override val userPassword: BehaviorSubject<String> = BehaviorSubject.create()

    override val userFirstName: BehaviorSubject<String> = BehaviorSubject.create()
    override val userSurname: BehaviorSubject<String> = BehaviorSubject.create()
    override val userNickname: BehaviorSubject<String> = BehaviorSubject.create()
    override val userProfilePhoto: BehaviorSubject<Bitmap> = BehaviorSubject.create()
    override val userProfilePhotoState: PublishSubject<ProfilePictureState> = PublishSubject.create()
    override val userPhoneNumber: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    override val userAddressLine: BehaviorSubject<String> = BehaviorSubject.create()
    override val userCity: BehaviorSubject<String> = BehaviorSubject.create()
    override val userZipCode: BehaviorSubject<String> = BehaviorSubject.create()
    override val userCountry: BehaviorSubject<String> = BehaviorSubject.create()
    override val permissionIntentDispatcher: PublishSubject<Int> = PublishSubject.create()


    override fun startUserVerification(flag: String) {
        userState.postValue(AuthInProgress(flag))
        userEmail.hide()
            .firstElement()
            .subscribe {
                verifyUserEmail(it, flag)
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

       Observable.combineLatest(userEmail.hide(), userPassword.hide(),
            BiFunction<String, String, Pair<String, String>> { email, password ->
                Pair(email, password)
            })
            .firstElement()
            .subscribe { userCredentials ->
                registerNewUser(userCredentials.first, userCredentials.second)
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

        val userId = firebaseAuth.currentUser?.uid
        val dbUserReference = firestoreDb.collection("users").document(userId!!)

        Observable.combineLatest(userFirstName.hide(),
            userSurname.hide(),
            userNickname.hide(),
            userEmail.hide(),
            Function4<String, String, String, String, NewUser> { firstName, surname, nickname, email ->
                NewUser(firstName, surname, nickname, email)
            })
            .firstElement()
            .subscribe { newUser ->
                dbUserReference.set(newUser)
                    .addOnSuccessListener { userState.postValue(SetUserProfilePicture) }
                    .addOnFailureListener { Log.w(TAG, it.message) }
            }.let { disposables.add(it) }
    }

    override fun uploadUserProfilePicture() {
        userState.postValue(UploadUserProfilePicture)

        val userId = firebaseAuth.currentUser?.uid
        userId?.let {uid ->
            val fileName = "${System.currentTimeMillis()}.bmp"
            val storageReference = firebaseStorage.reference.child("$uid/profile_photo/$fileName")
            storageReference.putBytes(preparePicture())
                .addOnSuccessListener {
                    savePhotoName(fileName)
                    userState.postValue(CollectContactInfo)
                }
                .addOnFailureListener{
                    userState.postValue(DataUpdateFailed(it.message ?: ERROR_UNSPECIFIED))
                }
        }
    }

    private fun savePhotoName(fileName: String) {
        val userId = firebaseAuth.currentUser?.uid
        val dbReference = firestoreDb.collection("users").document(userId!!).collection("profile_photo_reference").document(userId)
        dbReference.set(mapOf(Pair("photoReference", fileName)))
    }

    private fun preparePicture(): ByteArray {
        userProfilePhoto.hide().blockingFirst().let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return stream.toByteArray()
        }
    }

    override fun finishRegistration() {

        userState.postValue(SubmitContactInfo)

        val userId = firebaseAuth.currentUser?.uid
        val dbReference = firestoreDb.collection("users").document(userId!!).collection("contact_info").document(userId)

        Observable.combineLatest(userPhoneNumber.hide(),
            userAddressLine.hide(),
            userCity.hide(),
            userZipCode.hide(),
            userCountry.hide(),
            Function5<String, String, String, String, String, UserContactData> {phoneNo, addressLine, city, zipCode, country ->
                UserContactData(phoneNo, addressLine, city, zipCode, country)
            })
            .firstElement()
            .subscribe {contactData ->
                dbReference.set(contactData)
                    .addOnSuccessListener { userState.postValue(RegistrationFinalized) }
                        //TODO: Create a general error message in case the exception does not provide one
                    .addOnFailureListener { userState.postValue(DataUpdateFailed(it.message ?: ERROR_UNSPECIFIED)) }
            }.let { disposables.add(it) }
    }

    override fun startUserLogin() {
        Observable.combineLatest(userEmail.hide(), userPassword.hide(),
            BiFunction<String, String, Pair<String, String>> { email, password ->
                Pair(email, password)
            })
            .firstElement()
            .subscribe { userCredentials ->
                loginUser(userCredentials.first, userCredentials.second)
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