package com.hbs.invito.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionEnvironment {
    const val READ_GALLERIES = 101
}

object PermissionWhiteList {
    val SDK29 = listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

fun Activity.checkAndRequestPermissions(permissions: List<String>, requestCode: Int) {
    val needForRequestPermissions = permissions
        .filter { permission -> isNeedForRequestPermission(this, permission) }
        .toTypedArray()

    requestPermissions(needForRequestPermissions, requestCode)
}

private fun isNeedForRequestPermission(context: Context, permission: String): Boolean {
    return when (Build.VERSION.SDK_INT) {
        Build.VERSION_CODES.Q -> {
            val isWhitePermissionWhiteList = PermissionWhiteList.SDK29.contains(permission)
            val isCheckSelfPermission = ContextCompat.checkSelfPermission(context, permission)
            return !(isWhitePermissionWhiteList && isCheckSelfPermission == PackageManager.PERMISSION_GRANTED)
        }
        else -> true
    }
}