package com.example.vapeshop.presentation.common

import android.app.Application
import com.example.vapeshop.R
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VapeShopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val key = getString(R.string.yandex_api_key)
        MapKitFactory.setApiKey(key)
        MapKitFactory.initialize(this)
    }
}