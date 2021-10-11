package com.hbs.invito.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hbs.invito.R
import com.hbs.invito.databinding.LoginActivityBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        binding.run {
            lifecycleOwner = this@LoginActivity
        }
    }
}