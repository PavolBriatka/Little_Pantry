package com.briatka.pavol.littlepantry.di

import android.app.Application
import com.briatka.pavol.littlepantry.ConnectivityLiveData
import com.briatka.pavol.littlepantry.DisplayableUserLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideStorageInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideConnectivityLiveData(application: Application): ConnectivityLiveData {
        return ConnectivityLiveData(application)
    }

    @Singleton
    @Provides
    fun provideDisplayableUserLiveData(firebaseAuth: FirebaseAuth,
                                       firestore: FirebaseFirestore,
                                       firebaseStorage: FirebaseStorage): DisplayableUserLiveData {
        return DisplayableUserLiveData(firebaseAuth, firestore, firebaseStorage)
    }
}