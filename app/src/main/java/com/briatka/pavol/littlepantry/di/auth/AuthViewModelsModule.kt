package com.briatka.pavol.littlepantry.di.auth

import androidx.lifecycle.ViewModel
import com.briatka.pavol.littlepantry.di.ViewModelKey
import com.briatka.pavol.littlepantry.ui.auth.viewmodel.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel
}