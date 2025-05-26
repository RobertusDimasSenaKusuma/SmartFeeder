package com.example.smartfeeder

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class StockFeederActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_feeder)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Transparent status bar
        setupTransparentStatusBar()

        // Initialize ViewPager and TabLayout
        viewPager = findViewById(R.id.stockViewPager)
        tabLayout = findViewById(R.id.stockTabLayout)

        // Set up the ViewPager with the adapter
        val adapter = StockPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager)

        // Set up back button
        findViewById<View>(R.id.btn_back).setOnClickListener {
            handleBackButton()
        }
    }

    private fun setupTransparentStatusBar() {
        window.apply {
            statusBarColor = Color.TRANSPARENT

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
    }

    private fun handleBackButton() {
        if (viewPager.currentItem > 0) {
            // If not on first tab, go to previous tab
            viewPager.currentItem = viewPager.currentItem - 1
        } else {
            // If on first tab, close activity
            finish()
            finish()
            overridePendingTransition(0, 0)
        }
    }
}