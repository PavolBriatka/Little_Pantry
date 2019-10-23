package com.briatka.pavol.littlepantry.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthUserState.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth) : ViewModel(),
    ViewModelContract {

    companion object {
        const val TAG = "AuthViewModel"
        const val ERROR_UNSPECIFIED = "error_unspecified"
        private const val LOGIN_FLAG = "login_flag"
        private const val REGISTER_FLAG = "register_flag"
    }

    private val disposables = CompositeDisposable()
    val userState = MutableLiveData<AuthUserState>()

    override val loginEmail: BehaviorSubject<String> = BehaviorSubject.create()
    override val loginPassword: BehaviorSubject<String> = BehaviorSubject.create()
    override val registerEmail: BehaviorSubject<String> = BehaviorSubject.create()
    override val registerPassword: BehaviorSubject<String> = BehaviorSubject.create()

    init {
        Log.d(TAG, "view model init...")
    }

    override fun startUserVerification(flag: String) {
        userState.postValue(AuthInProgress)
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
        if (email.isNotBlank()) {
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
                                userState.postValue(AuthUserAlreadyExist)
                            } else {
                                userState.postValue(AuthCreateNewUser)
                            }
                        }
                    }
                }
            }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message)
                    userState.postValue(
                        AuthEmailVerificationFailure(
                            exception.message ?: ERROR_UNSPECIFIED
                        )
                    )
                }
        }
    }

    override fun startUserRegistration() {
        Log.e(TAG, "${registerEmail.value}y, ${registerPassword.value}x")
        Observable.combineLatest(registerEmail.hide(), registerPassword.hide(),
            BiFunction<String, String, Pair<String,String>> { email, password ->
                Pair(email, password)
            })
            .firstElement()
            .subscribe { userCredentials ->
                registerNewUser(userCredentials.first, userCredentials.second)
            }.let { disposables.add(it) }
    }

    private fun registerNewUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    userState.postValue(AuthRegistrationSuccessful)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure ${task.exception}")
                    userState.postValue(AuthRegistrationFailure(task.exception?.message ?: ERROR_UNSPECIFIED))
                }

            }
    }

    override fun finishRegistration() {
        userState.postValue(AuthRegistrationFinalized)
    }

    override fun startUserLogin() {
        Observable.combineLatest(loginEmail.hide(),loginPassword.hide(),
            BiFunction<String,String, Pair<String,String>> { email, password ->
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
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    userState.postValue(AuthLoginFailure(task.exception?.message ?: ERROR_UNSPECIFIED))
                }
            }
    }

}