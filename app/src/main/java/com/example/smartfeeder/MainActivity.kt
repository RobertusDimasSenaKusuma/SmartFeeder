package com.example.smartfeeder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smartfeeder.Fragment.DeviceFragment
import com.example.smartfeeder.Fragment.HomeFragment
import com.example.smartfeeder.Fragment.SettingsFragment
import com.example.smartfeeder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.device -> replaceFragment(DeviceFragment())
                R.id.settings -> replaceFragment(SettingsFragment())

                else -> {

                }
            }

            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainLayout,fragment)
        fragmentTransaction.commit()
    }
}