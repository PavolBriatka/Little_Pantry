package com.briatka.pavol.littlepantry.ui.auth.viewmodel

sealed class UserState {

    class AuthInProgress(val flag: String): UserState()
    class AuthEmailVerificationFailure(val error: String): UserState()

    //for login
    object AuthUserNotExist: UserState()
    object AuthWrongPassword: UserState()
    object AuthLoginSuccessful: UserState()
    class AuthLoginFailure(val error: String): UserState()

    //for registration
    object UserAlreadyExist: UserState()
    object CreateNewUser: UserState()
    object StartUserRegistration: UserState()
    object RegistrationSuccessful: UserState()
    class RegistrationFailure(val error: String): UserState()
    object UpdateUserProfilePicture: UserState() // waiting for user to prepare his photo
    object UploadUserProfilePicture: UserState() // when user submits photo for upload
    object ProfilePictureUploadSuccessful: UserState()
    class ProfilePictureUploadFailure(val error: String): UserState()
    object RegistrationFinalized: UserState()

}