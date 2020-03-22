package com.briatka.pavol.littlepantry.di.main

import com.briatka.pavol.littlepantry.ui.main.fragments.PostsFragment
import com.briatka.pavol.littlepantry.ui.main.fragments.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributePostsFragment(): PostsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment
}