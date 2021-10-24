package com.hbs.invito.ui.gallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hbs.invito.data.*
import com.hbs.invito.domain.model.MediaRepository
import com.hbs.invito.domain.model.MediaRepositoryImpl
import com.hbs.invito.mappers.toPresentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryViewModel : ViewModel() {
    private val _images = MutableLiveData<RecyclerViewMediaImageList>()
    val images: LiveData<RecyclerViewMediaImageList> = _images

    private val _folders = MutableLiveData<MediaStoreFolderList>()
    val folders: LiveData<MediaStoreFolderList> = _folders

    private val _selectImages = MutableLiveData<RecyclerViewMediaImageList>(listOf())
    val selectImages: LiveData<RecyclerViewMediaImageList> = _selectImages

    private val mediaRepository: MediaRepository = MediaRepositoryImpl()

    suspend fun queryImageFolders(applicationContext: Context) {
        //TODO : applicationContext는 추후에 DI로 대체될 것.
        withContext(Dispatchers.IO) {
            mediaRepository
                .queryAllMediaStoreFolders(applicationContext)
                ?.let { _folders.postValue(it) }
        }
    }

    suspend fun queryAllImage(applicationContext: Context) {
        withContext(Dispatchers.IO) {
            mediaRepository
                .queryAllImages(applicationContext)
                ?.map { it.toPresentation() }
                ?.toList()
                ?.let { _images.postValue(it) }
        }
    }

    fun selectGalleryImage(image: MediaStoreImagePresentation) {
        val selectGalleries = selectImages.value?.toMutableList() ?: return
        val selectGallery =
            selectGalleries.firstOrNull { it.item.contentUri == image.item.contentUri }

        if (selectGallery == null) {
            val newSelectGallery = image.apply {
                isVisible = true
                labeling = selectGalleries.size.toString()
            }
            selectGalleries += newSelectGallery
        } else {
            var isFindSelectGallery = false
            val oldSelectGallery = image.apply {
                isVisible = false
                labeling = "0"
            }

            selectGalleries
                .forEachIndexed { index, recyclerViewItem ->
                    recyclerViewItem.labeling = if (isFindSelectGallery) {
                        (index - 1).toString()
                    } else {
                        isFindSelectGallery = findSelectGallery(oldSelectGallery, recyclerViewItem)
                        index.toString()
                    }
                }
            selectGalleries -= oldSelectGallery
        }
        _selectImages.value = selectGalleries.toList()
    }

    private fun findSelectGallery(
        oldItem: MediaStoreImagePresentation,
        newItem: MediaStoreImagePresentation
    ): Boolean {
        return oldItem == newItem
    }
}