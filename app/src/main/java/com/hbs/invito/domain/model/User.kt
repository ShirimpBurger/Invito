package com.hbs.invito.domain.model

import android.net.Uri

data class User(
    val id:String,
    val email:String,
    val displayName:String,
    val photoUrl: Uri?
)