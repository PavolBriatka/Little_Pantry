package com.briatka.pavol.littlepantry.di

import androidx.lifecycle.ViewModelProvider
import com.briatka.pavol.littlepantry.viewmodels.ViewModelsProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelsProviderFactory ): ViewModelProvider.Factory
}