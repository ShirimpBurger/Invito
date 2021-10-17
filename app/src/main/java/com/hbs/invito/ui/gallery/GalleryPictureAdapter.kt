package com.hbs.invito.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hbs.invito.data.MediaStoreImage
import com.hbs.invito.databinding.GalleryPictureItemBinding

class GalleryPictureAdapter :
    ListAdapter<MediaStoreImage, GalleryPictureAdapter.ViewHolder>(galleryDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GalleryPictureItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: GalleryPictureItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val widthOfGalleryPicture by lazy {
            (binding.root.resources.displayMetrics.run { widthPixels / density }).toInt()
        }

        init {
            binding.ivGalleryPicture.layoutParams = ConstraintLayout.LayoutParams(widthOfGalleryPicture, widthOfGalleryPicture)
        }

        fun bind(media: MediaStoreImage) {
            Glide
                .with(binding.root)
                .load(media.contentUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(widthOfGalleryPicture, widthOfGalleryPicture)
                .into(binding.ivGalleryPicture)
        }
    }

}

val galleryDiffUtil = object : DiffUtil.ItemCallback<MediaStoreImage>() {
    override fun areItemsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage): Boolean {
        return oldItem.contentUri == newItem.contentUri
    }

    override fun areContentsTheSame(oldItem: MediaStoreImage, newItem: MediaStoreImage): Boolean {
        return oldItem.contentUri == newItem.contentUri
    }
}