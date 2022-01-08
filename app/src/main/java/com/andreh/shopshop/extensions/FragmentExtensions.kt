package com.andreh.shopshop.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun Fragment.ioLaunch(unit: () -> Unit) {
    this.viewLifecycleOwner.lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            unit.invoke()
        }
    }
}