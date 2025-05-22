package com.example.smartfeeder

import android.content.Intent
import android.content.SharedPreferences
// import android.content.SharedPreferences // Tidak kita gunakan untuk cek login Firebase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 2000L // 2 seconds
    // PREF_NAME dan IS_FIRST_TIME_LAUNCH masih bisa berguna jika Anda tetap ingin
    // menampilkan onboarding hanya sekali, terlepas dari status login.
    // Namun, untuk logika login, kita akan fokus pada FirebaseAuth.
     private val PREF_NAME = "SmartFeederPrefs"
     private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tidak perlu setContentView(R.layout.activity_splash) jika Anda ingin layar benar-benar kosong
        // atau jika Anda mengatur background programatik/tema. Jika ada layout splash, biarkan.
        setContentView(R.layout.activity_splash) // Asumsikan Anda punya layout splash

        auth = FirebaseAuth.getInstance() // Inisialisasi FirebaseAuth

        // Transparan status bar
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
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hapus LIGHT_STATUS_BAR jika background gelap
                // Jika background splash terang, tambahkan: or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        // Delayed navigation
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserLoginStatus()
        }, SPLASH_DELAY)
    }

    private fun checkUserLoginStatus() {
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            // Pengguna sudah login, arahkan ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Pengguna belum login, arahkan ke AuthActivity
            // Di sini Anda bisa menggabungkan logika checkFirstTimeLaunch jika masih relevan
            // Untuk sekarang, kita langsung ke AuthActivity jika tidak ada user.
            startActivity(Intent(this, AuthActivity::class.java))

            // Jika Anda masih ingin logika onboarding sebelum AuthActivity untuk pengguna baru (belum login):
            // checkFirstTimeLaunch()
        }
        finish() // Tutup SplashActivity agar tidak kembali ke sini dengan tombol back
    }

    // Jika Anda masih ingin menggunakan logika onboarding terpisah sebelum ke AuthActivity
    // untuk pengguna yang belum pernah launch DAN belum login.
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
        finish() // Pastikan finish dipanggil di sini juga
    }
}