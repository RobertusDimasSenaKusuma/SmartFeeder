package com.example.smartfeeder

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View // Import View
import android.widget.ImageButton // Import ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
// HAPUS: import androidx.compose.ui.semantics.text // Tidak digunakan dan berpotensi dari Compose
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
// HAPUS: import androidx.glance.visibility // Ini yang menyebabkan error terakhir
import com.example.smartfeeder.Fragment.DeviceFragment
import com.example.smartfeeder.Fragment.HomeFragment
import com.example.smartfeeder.Fragment.SettingsFragment
import com.example.smartfeeder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var defaultActionBarBackground: Drawable? = null
    private var defaultActionBarTitle: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let { actionBar ->
            // Mengambil background dari atribut tema atau fallback ke warna
            val typedArray = theme.obtainStyledAttributes(intArrayOf(android.R.attr.background))
            defaultActionBarBackground = typedArray.getDrawable(0)
            typedArray.recycle()

            if (defaultActionBarBackground == null) { // Fallback jika tidak ada background di tema ActionBar
                defaultActionBarBackground = ColorDrawable(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary))
            }
            defaultActionBarTitle = actionBar.title ?: getString(R.string.app_name)
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment()) // HomeFragment akan setup ActionBar-nya sendiri via onViewCreated
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    // HomeFragment akan memanggil setupCustomHomeActionBar dari onViewCreated
                    true
                }
                R.id.device -> {
                    replaceFragment(DeviceFragment())
                    // Gunakan setupGeneralActionBar untuk DeviceFragment
                    setupGeneralActionBar(getString(R.string.title_device), showBackButton = false)
                    true
                }
                R.id.settings -> {
                    replaceFragment(SettingsFragment())
                    // Gunakan setupGeneralActionBar untuk SettingsFragment agar konsisten
                    setupGeneralActionBar(getString(R.string.title_settings), showBackButton = false)
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainLayout, fragment)
        fragmentTransaction.commit()
    }

    // Fungsi untuk setup ActionBar kustom untuk Home
    fun setupCustomHomeActionBar(greeting: String? = null, userName: String? = null) {
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false)

            val customViewLayout = layoutInflater.inflate(R.layout.action_bar_home, null)
            setCustomView(customViewLayout)
            setBackgroundDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.card_main)) // Background untuk Home

            val tvGreeting = customViewLayout.findViewById<TextView>(R.id.tvGreeting)
            val tvUserName = customViewLayout.findViewById<TextView>(R.id.tvUserNamePlaceholder)

            tvGreeting.text = greeting ?: "SmartFeeder" // Default jika null
            tvUserName.text = userName ?: "Pet Owner"    // Default jika null
        }
        invalidateOptionsMenu()
    }

    // Fungsi ini masih bisa berguna jika ada fragment yang butuh ActionBar sangat standar
    fun setupDefaultActionBarFor(title: String) {
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(false)
            setDisplayShowTitleEnabled(true)
            setTitle(title.ifEmpty { defaultActionBarTitle ?: getString(R.string.app_name) })
            setBackgroundDrawable(defaultActionBarBackground) // Background default dari tema
            setDisplayHomeAsUpEnabled(false)
        }
        invalidateOptionsMenu()
    }

    // Fungsi untuk ActionBar General (digunakan oleh DeviceFragment dan SettingsFragment)
    fun setupGeneralActionBar(title: String, showBackButton: Boolean = true) {
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false) // Sembunyikan judul default ActionBar
            setDisplayHomeAsUpEnabled(false) // Kita gunakan tombol custom di layout

            val customView = layoutInflater.inflate(R.layout.action_bar_general, null)
            setCustomView(customView)

            // Atur background ActionBar menggunakan card_main.xml
            setBackgroundDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.card_main))

            val tvTitle = customView.findViewById<TextView>(R.id.tv_action_bar_title)
            val ibBack = customView.findViewById<ImageButton>(R.id.ib_action_bar_back)

            tvTitle.text = title

            if (showBackButton) {
                ibBack.visibility = View.VISIBLE
                ibBack.setOnClickListener {
                    onBackPressedDispatcher.onBackPressed() // Aksi kembali standar
                }
            } else {
                ibBack.visibility = View.GONE
                // Pertimbangkan untuk menambahkan margin start ke tvTitle jika ibBack GONE
                // atau pastikan layout action_bar_general menangani ini (misalnya, TextView di-center)
                // Jika tvTitle sudah di RelativeLayout dengan layout_centerInParent="true", ini seharusnya sudah baik.
            }
        }
        invalidateOptionsMenu()
    }
}