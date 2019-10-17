package com.briatka.pavol.littlepantry.di.auth

import com.briatka.pavol.littlepantry.ui.auth.fragments.LoginFragment
import com.briatka.pavol.littlepantry.ui.auth.fragments.RegistrationFormFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFormFragment(): RegistrationFormFragment
}