package com.hbs.invito.extensions.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hbs.invito.R
import com.hbs.invito.data.MediaStoreImage
import com.hbs.invito.data.RecyclerViewMediaImageList

@BindingAdapter(value = ["galleryImage"])
fun ImageView.setGalleryImage(mediaStoreImage: MediaStoreImage) {
    Glide
        .with(this)
        .load(mediaStoreImage.contentUri)
        .transition(DrawableTransitionOptions.withCrossFade())
        .override(width, width)
        .into(this)
}


@BindingAdapter(value = ["pickGalleryImage"])
fun ImageView.setPickGalleryImage(storeImageList: RecyclerViewMediaImageList?) {
    val mediaUri = if(storeImageList == null || storeImageList.isEmpty()) {
        R.drawable.tab_click
    } else {
        storeImageList.last().item.contentUri
    }

    Glide
        .with(this)
        .load(mediaUri)
        .transition(DrawableTransitionOptions.withCrossFade(125))
        .into(this)
}