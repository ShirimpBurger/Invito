package com.hbs.invito.data

import android.net.Uri

data class MediaStoreFolder(
    val id: Long,
    val displayName: String,
    val contentUri: Uri
)