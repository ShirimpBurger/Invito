package com.hbs.invito.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hbs.invito.databinding.GalleryPicturePickFragmentBinding
import com.hbs.invito.extensions.ViewClickDataCallback

class GalleryPicturePickFragment private constructor() : Fragment() {
    private val galleryPictureAdapter = GalleryPictureAdapter().apply {
        setHasStableIds(true)
    }

    private val binding by lazy {
        GalleryPicturePickFragmentBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(GalleryViewModel::class.java)
    }

    companion object {
        const val SPAN_COUNT_KEY = "spanCount"

        fun newInstance(spanCount: Int = 0): GalleryPicturePickFragment {
            val bundle = Bundle().apply {
                putInt(SPAN_COUNT_KEY, spanCount)
            }
            return GalleryPicturePickFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context == null) {
            return
        }
        arguments?.let { initView(it) }
        observeViewModel()
    }

    private fun initView(bundle: Bundle) {
        val spanCount = bundle.getInt(SPAN_COUNT_KEY, 4)
        binding.rvGalleryPictures.layoutManager = GridLayoutManager(context, spanCount)
        binding.rvGalleryPictures.adapter = galleryPictureAdapter

        galleryPictureAdapter.callback = ViewClickDataCallback { _, item ->
            viewModel.selectGalleryImage(item)
            galleryPictureAdapter.notifyDataSetChanged()
        }

        val scaleGestureDetector = ScaleGestureDetector(
            requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                var scaleFactor = 1f
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    if (scaleFactor >= 1.40) {
                        plusSpanCount()
                        scaleFactor = 1f
                    }
                    if (scaleFactor <= 0.60) {
                        reduceSpanCount()
                        scaleFactor = 1f
                    }
                    return super.onScale(detector)
                }
            })

        binding.rvGalleryPictures.setOnTouchListener { _, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            return@setOnTouchListener false
        }

    }

    private fun observeViewModel() {
        viewModel.images.observe(viewLifecycleOwner, {
            galleryPictureAdapter.submitList(it)
        })
    }

    private fun plusSpanCount() {
        val layoutManager = binding.rvGalleryPictures.layoutManager as GridLayoutManager
        layoutManager.spanCount = if (layoutManager.spanCount + 1 > 8) {
            8
        } else {
            layoutManager.spanCount + 1
        }
    }

    private fun reduceSpanCount() {
        val layoutManager = binding.rvGalleryPictures.layoutManager as GridLayoutManager
        layoutManager.spanCount = if (layoutManager.spanCount - 1 < 1) {
            1
        } else {
            layoutManager.spanCount - 1
        }
    }
}