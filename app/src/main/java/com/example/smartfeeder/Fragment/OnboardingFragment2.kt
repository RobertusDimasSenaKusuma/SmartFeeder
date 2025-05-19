package com.example.smartfeeder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class OnboardingFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding2, container, false)

        // Set click listener for the "Get Started" button
        val btnGetStarted: MaterialButton = view.findViewById(R.id.btnGetStarted2)
        btnGetStarted.setOnClickListener {
            // This is the last fragment, so navigate to the main activity
            (activity as? OnboardingActivity)?.skipOnboarding()
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