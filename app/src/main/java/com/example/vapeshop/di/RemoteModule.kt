package com.example.vapeshop.di

import com.example.vapeshop.data.remote.FirebaseRemoteDataSource
import com.example.vapeshop.data.remote.RemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): RemoteDataSource {
        return FirebaseRemoteDataSource(firebaseAuth, firestore)
    }
}