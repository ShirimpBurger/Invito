package com.hbs.invito.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hbs.invito.databinding.GalleryPictureItemBinding
import com.hbs.invito.data.MediaStoreImagePresentation
import com.hbs.invito.extensions.ViewClickDataCallback

class GalleryPictureAdapter(var callback:ViewClickDataCallback<MediaStoreImagePresentation>? = null) :
    ListAdapter<MediaStoreImagePresentation, GalleryPictureAdapter.ViewHolder>(galleryDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GalleryPictureItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).item.id
    }

    inner class ViewHolder(private val binding: GalleryPictureItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.callback = callback
        }

        fun bind(presentation: MediaStoreImagePresentation) {
            binding.presentation = presentation
        }
    }

}

val galleryDiffUtil = object : DiffUtil.ItemCallback<MediaStoreImagePresentation>() {
    override fun areItemsTheSame(
        oldItem: MediaStoreImagePresentation,
        newItem: MediaStoreImagePresentation
    ): Boolean {
        return oldItem.item.id == oldItem.item.id
    }

    override fun areContentsTheSame(
        oldItem: MediaStoreImagePresentation,
        newItem: MediaStoreImagePresentation
    ): Boolean {
        return oldItem.item == newItem.item
    }
}