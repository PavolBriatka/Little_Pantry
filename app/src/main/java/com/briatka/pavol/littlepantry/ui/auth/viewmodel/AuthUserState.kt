package com.briatka.pavol.littlepantry.ui.auth.viewmodel

sealed class AuthUserState {

    object AuthInProgress: AuthUserState()
    class AuthEmailVerificationFailure(val error: String): AuthUserState()

    //for login
    object AuthUserNotExist: AuthUserState()
    object AuthWrongPassword: AuthUserState()
    object AuthLoginSuccessful: AuthUserState()

    //for registration
    object AuthUserAlreadyExist: AuthUserState()
    object AuthCreateNewUser: AuthUserState()
    object AuthRegistrationSuccessful: AuthUserState()
    class AuthRegistrationFailure(val error: String): AuthUserState()
    object AuthRegistrationFinalized: AuthUserState()

}