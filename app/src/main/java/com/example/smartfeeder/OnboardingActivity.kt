package com.example.smartfeeder

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.viewpager.widget.ViewPager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Transparan status bar
        window.apply {
            statusBarColor = Color.TRANSPARENT

            // Atur appearance status bar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setDecorFitsSystemWindows(false)
                insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        viewPager = findViewById(R.id.viewPager)
        val adapter = OnboardingPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
    }

    // Method to navigate to the next fragment
    fun moveToNextFragment() {
        if (viewPager.currentItem < 1) { // We have 2 fragments (0 and 1)
            viewPager.currentItem = viewPager.currentItem + 1
        } else {
            // Navigate to auth activity when the last onboarding fragment is completed
            navigateToAuthActivity()
        }
    }

    // Method to skip all onboarding fragments
    fun skipOnboarding() {
        navigateToAuthActivity()
    }

    // Method to navigate to authentication activity
    private fun navigateToAuthActivity() {
        // Start AuthActivity and finish this activity
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}