package com.briatka.pavol.littlepantry.di.auth

import com.briatka.pavol.littlepantry.ui.auth.fragments.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment
}