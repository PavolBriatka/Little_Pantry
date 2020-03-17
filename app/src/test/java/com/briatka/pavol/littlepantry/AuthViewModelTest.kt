package com.briatka.pavol.littlepantry

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel

    @Mock
    lateinit var firebaseAuth: FirebaseAuth

    @Mock
    lateinit var firestoreDb: FirebaseFirestore

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {

        viewModel = AuthViewModel(firebaseAuth, firestoreDb)
    }

    @Test
    fun starUserRegistration_userStateUpdated() {
        //Arrange
        //Act
        viewModel.startUserRegistration()
        //Assert
        Assert.assertTrue(viewModel.userState.value == UserState.StartUserRegistration)
    }

    @Test
    fun startUserRegistration_createUserMethodInvoked() {
        //Arrange
        prepareCredentials()
        //Act
        viewModel.startUserRegistration()
        //Assert
        verify(firebaseAuth, times(1)).createUserWithEmailAndPassword(any(), any())
    }

    private fun prepareCredentials() {
        viewModel.userEmail.onNext("a@b.com")
        viewModel.userPassword.onNext("123456789")
    }

    /*private fun prepareAuthResponse() {
        Mockito.`when`(firebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(
            //Task<AuthResult>()
        )
    }*/
}