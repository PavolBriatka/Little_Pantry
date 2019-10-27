package com.briatka.pavol.littlepantry.di

import com.briatka.pavol.littlepantry.SplashActivity
import com.briatka.pavol.littlepantry.di.auth.AuthFragmentBuildersModule
import com.briatka.pavol.littlepantry.di.auth.AuthViewModelsModule
import com.briatka.pavol.littlepantry.di.main.MainFragmentBuildersModule
import com.briatka.pavol.littlepantry.di.main.MainModule
import com.briatka.pavol.littlepantry.di.main.MainViewModelsModule
import com.briatka.pavol.littlepantry.ui.auth.AuthActivity
import com.briatka.pavol.littlepantry.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector(
        modules = [AuthViewModelsModule::class,
            AuthFragmentBuildersModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector(
        modules = [MainModule::class,
            MainViewModelsModule::class,
            MainFragmentBuildersModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}