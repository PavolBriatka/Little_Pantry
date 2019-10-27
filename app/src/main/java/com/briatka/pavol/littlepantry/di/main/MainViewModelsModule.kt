package com.briatka.pavol.littlepantry.di.main

import androidx.lifecycle.ViewModel
import com.briatka.pavol.littlepantry.di.ViewModelKey
import com.briatka.pavol.littlepantry.ui.main.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

}