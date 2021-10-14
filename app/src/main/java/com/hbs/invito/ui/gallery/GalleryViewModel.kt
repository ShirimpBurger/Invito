package com.hbs.invito.ui.gallery

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.invitogallery.utils.MediaStoreFolderList
import com.example.invitogallery.utils.MediaStoreImageList
import com.hbs.invito.data.MediaStoreFolder
import com.hbs.invito.data.MediaStoreImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class GalleryViewModel : ViewModel() {
    private val _images = MutableLiveData<MediaStoreImageList>()
    val images: LiveData<MediaStoreImageList> = _images

    private val _folders = MutableLiveData<MediaStoreFolderList>()
    val folders: LiveData<MediaStoreFolderList> = _folders

    suspend fun queryImageFolders(contentResolver: ContentResolver) {
        val folders = mutableListOf<MediaStoreFolder>()
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

            val sortOrder = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} DESC"

            val query = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val folder = MediaStoreFolder(id, displayName, contentUri)
                    folders += folder
                }
                _folders.postValue(folders)
            }
        }
    }

    suspend fun queryAllImage(contentResolver: ContentResolver): List<MediaStoreImage> {
        val images = mutableListOf<MediaStoreImage>()
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC "

            val query = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val dateModified =
                        Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val image = MediaStoreImage(id, displayName, dateModified, contentUri)
                    images += image
                }
                _images.postValue(images)
            }
        }

        return images
    }

    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").parse("$day.$month.$year")?.time ?: 0
}