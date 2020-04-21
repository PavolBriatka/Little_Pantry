package com.briatka.pavol.littlepantry.ui.splash

import android.content.Intent
import androidx.lifecycle.Observer
import com.briatka.pavol.littlepantry.DisplayableUserLiveData
import com.briatka.pavol.littlepantry.ui.auth.AuthActivity
import com.briatka.pavol.littlepantry.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var userLiveData: DisplayableUserLiveData


    override fun onStart() {
        super.onStart()
        isUserLoggedIn()
    }

    private fun isUserLoggedIn() {
        if (firebaseAuth.currentUser != null) {
            observeUserData()
        } else {
            launchLoginScreen()
        }
    }

    private fun observeUserData() {
        userLiveData.observe(this, Observer { user ->
            launchMainActivity()
        })
    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchLoginScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}