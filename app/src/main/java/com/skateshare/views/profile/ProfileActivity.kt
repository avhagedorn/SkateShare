package com.skateshare.views.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.skateshare.R

class ProfileActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SkateShare)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}   