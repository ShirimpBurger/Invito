package com.hbs.invito.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hbs.invito.R
import com.hbs.invito.ui.gallery.GalleryActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        startActivity(Intent(this, GalleryActivity::class.java))
    }
}