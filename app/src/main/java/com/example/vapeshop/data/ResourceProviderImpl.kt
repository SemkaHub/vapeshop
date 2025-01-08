package com.example.vapeshop.data

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.vapeshop.R
import com.example.vapeshop.domain.util.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getErrorImage(): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.error_image)
    }
}