package com.skateshare.views.Authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skateshare.R
import com.skateshare.views.Profile.ProfileActivity

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SkateShare)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (userLoggedIn()) {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun userLoggedIn() = this.getSharedPreferences("userData", Context.MODE_PRIVATE)
        .getBoolean("isLoggedIn", false)
}