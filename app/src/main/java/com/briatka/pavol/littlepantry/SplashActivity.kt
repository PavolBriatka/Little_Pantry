package com.briatka.pavol.littlepantry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.briatka.pavol.littlepantry.ui.auth.AuthActivity
import com.briatka.pavol.littlepantry.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("SplashActivity", "called")
        setContentView(R.layout.activity_splash)

    }

    override fun onStart() {
        super.onStart()
        isUserLoggedIn()
    }

    private fun isUserLoggedIn() {

        val intent = if (firebaseAuth.currentUser == null) {
            Intent(this, AuthActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()


    }
}