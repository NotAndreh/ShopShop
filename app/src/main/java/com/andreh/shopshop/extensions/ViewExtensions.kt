package com.andreh.shopshop.extensions

import android.view.View
import android.view.ViewTreeObserver

fun View.runWhenReady(action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}