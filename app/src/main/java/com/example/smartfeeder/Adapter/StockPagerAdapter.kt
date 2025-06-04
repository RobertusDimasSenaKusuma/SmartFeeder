package com.example.smartfeeder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class StockPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StockFragment()
            else -> StockFragment() // You can add more fragments here if needed
        }
    }

    override fun getCount(): Int {
        return 1 // Only one tab for now (Stock)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Stock"
            else -> null
        }
    }
}