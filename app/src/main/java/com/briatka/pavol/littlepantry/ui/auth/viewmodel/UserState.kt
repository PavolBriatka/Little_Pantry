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
    //(step I. - collect user input)
    object CreateNewUser: UserState()
    //(step I. - processing)
    object StartUserRegistration: UserState()
    //(step I. - success result (overlaps with) step II. - collect user input)
    object SetUserProfilePicture: UserState()
    //(step II. - processing)
    object UploadUserProfilePicture: UserState()
    // (step II. -  success result (overlaps with) step III. - collect user input)
    object CollectContactInfo: UserState()
    //(step III. - processing)
    object SubmitContactInfo: UserState()
    //(step III. - success result)
    object RegistrationFinalized: UserState()
    //(step I., II., III. - failed result)
    class DataUpdateFailed(val error: String): UserState()

}