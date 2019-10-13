package com.briatka.pavol.littlepantry.di

import com.briatka.pavol.littlepantry.SplashActivity
import com.briatka.pavol.littlepantry.di.auth.AuthFragmentBuildersModule
import com.briatka.pavol.littlepantry.di.auth.AuthViewModelsModule
import com.briatka.pavol.littlepantry.ui.auth.AuthActivity
import com.briatka.pavol.littlepantry.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
        modules = [AuthViewModelsModule::class,
        AuthFragmentBuildersModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}