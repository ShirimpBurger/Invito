package com.hbs.invito.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hbs.invito.databinding.ActivityGalleryBinding
import com.hbs.invito.extensions.PermissionEnvironment
import com.hbs.invito.extensions.ViewClickDataCallback
import com.hbs.invito.extensions.checkAndRequestPermissions
import kotlinx.coroutines.launch

class GalleryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityGalleryBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(this).get(GalleryViewModel::class.java)
    }
    private val galleryPictureAdapter = GalleryPictureAdapter().apply {
        setHasStableIds(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        observeViewModel()
        requestPermissionForReadGalleries()
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
                viewModel.queryAllImage(applicationContext)
                viewModel.queryImageFolders(applicationContext)
            }
        }
    }

    private fun initView() {
        binding.setGalleryCallback { view ->

        }

        binding.ivGalleryClose.setOnClickListener {
            finish()
        }

        binding.rvGalleryPictures.adapter = galleryPictureAdapter
        galleryPictureAdapter.callback = ViewClickDataCallback { _, item ->
            viewModel.selectGalleryImage(item)
            galleryPictureAdapter.notifyDataSetChanged()
        }
    }

    private fun observeViewModel() {
        viewModel.images.observe(this, {
            galleryPictureAdapter.submitList(it)
        })

        viewModel.folders.observe(this, { folders ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                folders.map { it.displayName }
            )
            binding.tvGalleryPath.setAdapter(adapter)
        })
    }

    private fun requestPermissionForReadGalleries() {
        lifecycleScope.launchWhenResumed {
            checkAndRequestPermissions(
                listOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PermissionEnvironment.READ_GALLERIES
            )
        }
    }
}

fun interface GalleryPathCallback {
    fun onClick(view: View)
}