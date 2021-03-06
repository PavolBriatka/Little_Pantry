package com.briatka.pavol.littlepantry.di.auth

import com.briatka.pavol.littlepantry.ui.auth.fragments.LoginFragment
import com.briatka.pavol.littlepantry.ui.auth.fragments.UserProfilePictureFragment
import com.briatka.pavol.littlepantry.ui.auth.fragments.RegistrationFormFragment
import com.briatka.pavol.littlepantry.ui.auth.fragments.UserContactInfoFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterUserProfileFragment(): UserProfilePictureFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFormFragment(): RegistrationFormFragment

    @ContributesAndroidInjector
    abstract fun contributeUserContacInfoFragment(): UserContactInfoFragment
}