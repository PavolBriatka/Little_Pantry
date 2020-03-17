package com.briatka.pavol.littlepantry.di.main

import android.app.Application
import android.content.res.Resources
import com.briatka.pavol.littlepantry.R
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

    @Provides
    fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Provides
    fun provideDrawerMenu(resources: Resources): List<String> {
        return listOf(
            resources.getString(R.string.profile_label),
            resources.getString(R.string.inbox_label),
            resources.getString(R.string.log_out_label)
        )
    }
}