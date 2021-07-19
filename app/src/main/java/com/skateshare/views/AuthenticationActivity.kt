package com.skateshare.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skateshare.R

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SkateShare)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (userLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun userLoggedIn() = this.getSharedPreferences("userData", Context.MODE_PRIVATE)
        .getBoolean("isLoggedIn", false)

    override fun onDestroy() {
        super.onDestroy()
        Log.i("AuthenticationActivity", "Destroyed")
    }
}