package com.hbs.invito.extensions

import android.view.View

fun interface ViewClickDataCallback<T> {
    fun onClick(view: View, data: T)
}