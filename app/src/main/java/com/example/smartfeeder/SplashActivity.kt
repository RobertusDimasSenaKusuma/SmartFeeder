package com.example.smartfeeder

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 2000L // 2 seconds
    private val PREF_NAME = "SmartFeederPrefs"
    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Transparan status bar
        window.apply {
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        // Delayed navigation
        Handler(Looper.getMainLooper()).postDelayed({
            checkFirstTimeLaunch()
        }, SPLASH_DELAY)
    }

    private fun checkFirstTimeLaunch() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val isFirstTimeLaunch = sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true)

        if (isFirstTimeLaunch) {
            // First time app launch, show onboarding
            sharedPreferences.edit().putBoolean(IS_FIRST_TIME_LAUNCH, false).apply()
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            // Not first time, go directly to auth
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }
}