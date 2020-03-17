package com.briatka.pavol.littlepantry.ui.auth.viewmodel

sealed class ProfilePictureState {

    object ProfilePictureOk: ProfilePictureState()
    class ProfilePictureError(val error: String): ProfilePictureState()
}