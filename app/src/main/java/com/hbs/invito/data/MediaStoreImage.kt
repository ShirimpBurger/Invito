package com.hbs.invito.data

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class MediaStoreImage(
    val id: Long,
    val displayName: String,
    val dateAdded: Date,
    val contentUri: Uri
)