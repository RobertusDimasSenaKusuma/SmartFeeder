package com.example.smartfeeder

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class AuthActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

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



        // Initialize ViewPager and TabLayout
        viewPager = findViewById(R.id.authViewPager)
        tabLayout = findViewById(R.id.authTabLayout)

        // Set up the ViewPager with the AuthPagerAdapter
        val adapter = AuthPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager)


    }
}