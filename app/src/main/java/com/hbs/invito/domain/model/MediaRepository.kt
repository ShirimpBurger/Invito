package com.hbs.invito.domain.model

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.hbs.invito.data.MediaStoreFolder
import com.hbs.invito.data.MediaStoreImage
import java.util.*
import java.util.concurrent.TimeUnit

interface MediaRepository {
    fun queryAllMediaStoreFolders(applicationContext: Context): List<MediaStoreFolder>?
    fun queryAllImages(applicationContext: Context): List<MediaStoreImage>?
}

class MediaRepositoryImpl : MediaRepository {

    override fun queryAllMediaStoreFolders(applicationContext: Context): List<MediaStoreFolder>? {
        val contentResolver = applicationContext.contentResolver
        val folders = mutableListOf<MediaStoreFolder>()
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

        return query?.use { cursor ->
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

            return@use folders
                .distinctBy { it.displayName }
                .toList()
        }
    }

    override fun queryAllImages(applicationContext: Context): List<MediaStoreImage>? {
        val contentResolver = applicationContext.contentResolver
        val images = mutableListOf<MediaStoreImage>()
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

        return query?.use { cursor ->
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

            return images
        }
    }
}