package com.briatka.pavol.littlepantry.di.auth

import android.app.Application
import android.content.res.Resources
import com.briatka.pavol.littlepantry.R
import dagger.Module
import dagger.Provides

@Module
class AuthModule {

    @Provides
    fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Provides
    fun provideRegistrationSteps(resources: Resources): List<String> {
        return listOf(
            resources.getString(R.string.registration_step_one),
            resources.getString(R.string.registration_step_two),
            resources.getString(R.string.registration_step_three)
        )
    }
}