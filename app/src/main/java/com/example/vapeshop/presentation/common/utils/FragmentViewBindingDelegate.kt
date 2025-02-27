package com.example.vapeshop.presentation.common.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingClass: (View) -> T
) : ReadOnlyProperty<Fragment, T> {

    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment) {
                    if (it != null) {
                        // viewLifecycleOwner доступен, но не факт что инициализирован
                        fragment.viewLifecycleOwner.lifecycle.addObserver(object :
                            DefaultLifecycleObserver {
                            override fun onDestroy(owner: LifecycleOwner) {
                                binding = null
                            }
                        })
                    }
                }
            }
        })
    }

    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T {
        val view =
            thisRef.view ?: throw IllegalStateException("View is null, cannot get viewBinding")
        return binding ?: viewBindingClass(view).also {
            if (fragment.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                binding = it
            }
        }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingClass: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingClass)