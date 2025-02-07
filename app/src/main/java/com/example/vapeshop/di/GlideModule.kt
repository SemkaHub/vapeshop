package com.example.vapeshop.di

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object GlideModule {

    @Provides
    fun provideGlideRequestManager(fragment: Fragment): RequestManager {
        return Glide.with(fragment)
    }
}