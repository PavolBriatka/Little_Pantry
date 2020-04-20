package com.briatka.pavol.littlepantry

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.briatka.pavol.littlepantry.models.DisplayableUser
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.UserState
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val stringSlot = mutableListOf<String>()
    private val taskListenerSlot = slot<OnCompleteListener<AuthResult>>()

    private val mockResultTask = mockk<Task<AuthResult>>()

    /* @Mock
     lateinit var firebaseAuth: FirebaseAuth

     @Mock
     lateinit var firestoreDb: FirebaseFirestore

     @Mock
     lateinit var firebaseStorage: FirebaseStorage*/

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {

        every { mockResultTask.isSuccessful } returns false

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
        auth = FirebaseAuth.getInstance()

        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        database = FirebaseFirestore.getInstance()

        mockkStatic(FirebaseStorage::class)
        every { FirebaseStorage.getInstance() } returns mockk(relaxed = true)
        storage = FirebaseStorage.getInstance()

        viewModel = AuthViewModel(auth, database, storage)
    }

    @Test
    fun starUserRegistration_userStateUpdated() {
        //Arrange
        //Act
        viewModel.startUserRegistration()
        //Assert
        assertTrue(viewModel.userState.value == UserState.StartUserRegistration)
    }

    @Test
    fun startUserRegistration_createUserMethodInvoked() {
        //Arrange
        prepareCredentials()

        //Act
        viewModel.startUserRegistration()

        //Assert
        verify { auth.createUserWithEmailAndPassword(capture(stringSlot), capture(stringSlot)) }
        assertTrue(stringSlot[0] == "a@b.com")
        assertTrue(stringSlot[1] == "123456789")
    }

    @Test
    fun startUserRegistration_failure_failedUserState() {
        //Arrange
        prepareCredentials()
        userRegistrationFailureMockInteractions()

        //Act
        viewModel.startUserRegistration()

        //Assert
        //First assert then the User state is of type DataUpdateFailed
        assertTrue(viewModel.userState.value is UserState.DataUpdateFailed )
        //Then we can cast the state to DataUpdateFailed class and verify the error message
        val userState = viewModel.userState.value as UserState.DataUpdateFailed
        assertTrue(userState.error == errorMessage)
    }

    //region Helpers

    private fun prepareCredentials() {
        viewModel.displayableUser.onNext(DisplayableUser(uEmail = "a@b.com", uPassword = "123456789"))
    }

    private fun userRegistrationFailureMockInteractions() {

        every { auth.createUserWithEmailAndPassword(any(), any()) } returns mockResultTask
        every { mockResultTask.exception }  returns Exception(errorMessage)
        every { mockResultTask.addOnCompleteListener(capture(taskListenerSlot)) } answers {

            taskListenerSlot.captured.onComplete(mockResultTask)
            mockResultTask
        }
    }

    private val errorMessage = "To err if human"

    //endregion



    /*private fun prepareAuthResponse() {
        Mockito.`when`(firebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(
            //Task<AuthResult>()
        )
    }*/
}