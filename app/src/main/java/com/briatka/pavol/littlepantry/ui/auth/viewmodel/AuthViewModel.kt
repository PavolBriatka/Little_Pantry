package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.briatka.pavol.littlepantry.models.NewUser
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState.*
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_EMAIL_ERROR
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.LOGIN_FLAG
import com.briatka.pavol.littlepantry.utils.AuthConstants.Companion.REGISTER_FLAG
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDb: FirebaseFirestore
) : ViewModel(),
    ViewModelContract {

    companion object {
        const val TAG = "AuthViewModel"
        const val ERROR_UNSPECIFIED = "error_unspecified"
    }

    private val disposables = CompositeDisposable()
    val authState = MutableLiveData<AuthUserState>()

    override val loginEmail: BehaviorSubject<String> = BehaviorSubject.create()
    override val loginPassword: BehaviorSubject<String> = BehaviorSubject.create()
    override val registerEmail: BehaviorSubject<String> = BehaviorSubject.create()
    override val registerPassword: BehaviorSubject<String> = BehaviorSubject.create()

    override val userFirstName: BehaviorSubject<String> = BehaviorSubject.create()
    override val userSurname: BehaviorSubject<String> = BehaviorSubject.create()
    override val userNickname: BehaviorSubject<String> = BehaviorSubject.create()

    init {
        Log.d(TAG, "view model init...")
    }

    override fun startUserVerification(flag: String) {
        authState.postValue(AuthInProgress)
        when (flag) {
            LOGIN_FLAG -> {
                loginEmail.hide()
                    .firstElement()
                    .subscribe {
                        verifyUserEmail(it, flag)
                    }.let { disposables.add(it) }
            }
            REGISTER_FLAG -> {
                registerEmail.hide()
                    .firstElement()
                    .subscribe {
                        verifyUserEmail(it, flag)
                    }.let { disposables.add(it) }
            }
        }

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
                            authState.postValue(AuthUserNotExist)
                        }
                    }
                }
                REGISTER_FLAG -> {
                    signInMethods?.let {
                        if (it.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                            authState.postValue(AuthUserAlreadyExist)
                        } else {
                            authState.postValue(AuthCreateNewUser)
                        }
                    }
                }
            }
        }
            .addOnFailureListener {
                authState.postValue(AuthEmailVerificationFailure(LOGIN_EMAIL_ERROR))
            }
    }

    override fun startUserRegistration() {
        Log.e(TAG, "${registerEmail.value}y, ${registerPassword.value}x")
        Observable.combineLatest(registerEmail.hide(), registerPassword.hide(),
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
                    authState.postValue(
                        AuthRegistrationFailure(
                            task.exception?.message ?: ERROR_UNSPECIFIED
                        )
                    )
                }

            }
    }

    private fun updateDbProfileInfo() {
        val userId = firebaseAuth.currentUser?.uid
        val dbUserReference = firestoreDb.collection("users").document(userId!!)
        Observable.combineLatest(userFirstName.hide(),
            userSurname.hide(),
            userNickname.hide(),
            registerEmail.hide(),
            Function4<String, String, String, String, NewUser> { firstName, surname, nickname, email ->
                NewUser(firstName, surname, nickname, email)
            })
            .firstElement()
            .subscribe { newUser ->
                dbUserReference.set(newUser)
                    .addOnSuccessListener { authState.postValue(AuthRegistrationSuccessful) }
                    .addOnFailureListener { Log.w(TAG, it.message) }
            }.let { disposables.add(it) }
    }

    override fun finishRegistration() {
        authState.postValue(AuthRegistrationFinalized)
    }

    override fun startUserLogin() {
        Observable.combineLatest(loginEmail.hide(), loginPassword.hide(),
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
                    authState.postValue(AuthLoginSuccessful)
                } else {
                    Log.e(TAG, "signInWithEmail:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        authState.postValue(AuthWrongPassword)
                    } else {
                        authState.postValue(
                            AuthLoginFailure(task.exception?.message ?: ERROR_UNSPECIFIED)
                        )
                    }
                }
            }
    }

}