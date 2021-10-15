package com.hbs.invito.ui.gallery

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hbs.invito.databinding.ActivityGalleryBinding
import com.hbs.invito.extensions.PermissionEnvironment
import com.hbs.invito.extensions.checkAndRequestPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityGalleryBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(this).get(GalleryViewModel::class.java)
    }
    private val galleryPictureAdapter = GalleryPictureAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()

        viewModel.images.observe(this, {
            galleryPictureAdapter.submitList(it)
        })

        viewModel.folders.observe(this, {
            val displayNames = it
                .map { folder -> folder.displayName }
                .toList()
                .distinct()

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayNames)
            binding.tvGalleryPath.setAdapter(adapter)
        })

        lifecycleScope.launchWhenResumed {
            checkAndRequestPermissions(
                listOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PermissionEnvironment.READ_GALLERIES
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "누락된 권한이 있습니다.", Toast.LENGTH_SHORT).show()
        }

        if (requestCode == PermissionEnvironment.READ_GALLERIES) {
            lifecycleScope.launch {
                viewModel.queryAllImage(contentResolver)
                viewModel.queryImageFolders(contentResolver)
            }
        }
    }

    private fun initView() {
        binding.setGalleryCallback { view ->

        }

        binding.tvDownload.setOnClickListener {
            clickDownloadView()
        }

        binding.rvGalleryPictures.adapter = galleryPictureAdapter
    }

    private fun clickDownloadView() {
        lifecycleScope.launch {
            val uri = viewModel.images.value?.get(0)?.contentUri
            uri?.let { insertImage(it) }
        }
    }

    private suspend fun insertImage(uri: Uri) {
        //수정 예정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            withContext(Dispatchers.IO) {
                val newFile = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, uri.path + "2")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }

                val newFileUri = contentResolver.insert(collection, newFile)
                    ?: throw Exception("MediaStore Uri couldn't be created")

                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }

                MediaStore.Images.Media.insertImage(contentResolver, bitmap, newFileUri.path, null)
            }
        }
    }
}

fun interface GalleryPathCallback {
    fun onClick(view: View)
}