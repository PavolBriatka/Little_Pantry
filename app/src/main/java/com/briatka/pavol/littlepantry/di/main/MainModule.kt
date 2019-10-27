package com.briatka.pavol.littlepantry.di.main

import com.briatka.pavol.littlepantry.ui.main.viewmodel.FirebaseAuthLiveData
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    fun provideFirebaseAuthLiveData(firebaseAuth: FirebaseAuth): FirebaseAuthLiveData {
        return FirebaseAuthLiveData(firebaseAuth)
    }
}