package com.briatka.pavol.littlepantry.ui.main

sealed class MainViewState {

    object ShowSplashActivity : MainViewState()
    object ShowLoginActivity : MainViewState()
    object ShowMainActivity : MainViewState()
}