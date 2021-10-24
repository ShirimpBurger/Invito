package com.hbs.invito.extensions.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hbs.invito.data.MediaStoreImage


@BindingAdapter(value = ["galleryImage"])
fun ImageView.setGalleryImage(mediaStoreImage: MediaStoreImage) {

    Glide
        .with(this)
        .load(mediaStoreImage.contentUri)
        .transition(DrawableTransitionOptions.withCrossFade())
        .override(width, width)
        .into(this)
}