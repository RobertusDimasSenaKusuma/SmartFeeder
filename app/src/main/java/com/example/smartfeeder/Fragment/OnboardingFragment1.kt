package com.example.smartfeeder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class OnboardingFragment1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding1, container, false)

        // Set click listener for the "Get Started" button
        val btnGetStarted: MaterialButton = view.findViewById(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            // Navigate to the next fragment when button is clicked
            (activity as? OnboardingActivity)?.moveToNextFragment()
        }

        // Set click listener for the "Skip" button
        val tvSkip: TextView = view.findViewById(R.id.tvSkip)
        tvSkip.setOnClickListener {
            // Skip all onboarding fragments
            (activity as? OnboardingActivity)?.skipOnboarding()
        }

        return view
    }
}