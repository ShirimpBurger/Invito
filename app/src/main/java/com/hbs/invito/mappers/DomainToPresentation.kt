package com.hbs.invito.mappers

import com.hbs.invito.data.MediaStoreImage
import com.hbs.invito.data.MediaStoreImagePresentation

fun MediaStoreImage.toPresentation() : MediaStoreImagePresentation {
    return MediaStoreImagePresentation(this)
}