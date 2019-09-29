package com.briatka.pavol.littlepantry.di

import com.briatka.pavol.littlepantry.di.auth.AuthViewModelsModule
import com.briatka.pavol.littlepantry.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
        modules = [AuthViewModelsModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity
}